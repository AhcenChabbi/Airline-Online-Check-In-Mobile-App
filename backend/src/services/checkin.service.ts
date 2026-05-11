import { prisma } from "../lib/prisma";
import AppError from "../utils/AppError";
import * as HTTP_STATUS from "../constants/http";
import { generateBoardingPassToken } from "../utils/boardingPass";
import { generateAndUploadQRCode } from "../utils/assets.js";

import type {
  PassportScanInput,
  SeatSelectionInput,
  BaggageInput,
  SpecialRequestsInput,
} from "../schemas/checkin.schema.js";

// ─── helpers ────────────────────────────────────────────────────────────────

function assertStep(current: string, expected: string, nextLabel: string) {
  if (current !== expected) {
    throw new AppError(
      `Check-in is at step "${current}". Expected "${expected}" to proceed with ${nextLabel}.`,
      HTTP_STATUS.CONFLICT,
    );
  }
}

// ─── Step 0: Initiate ───────────────────────────────────────────────────────

export async function initiateCheckIn(bookingId: string, passengerId: string) {
  // Validate booking exists and belongs to a real flight
  const booking = await prisma.booking.findUnique({
    where: { id: bookingId },
    include: { flight: true },
  });

  if (!booking) throw new AppError("Booking not found.", HTTP_STATUS.NOT_FOUND);
  if (booking.status === "CANCELLED")
    throw new AppError("Booking is cancelled.", HTTP_STATUS.BAD_REQUEST);

  // Enforce 24-hour check-in window
  const now = new Date();
  const departure = booking.flight.departureAt;
  const hoursUntilDeparture = (departure.getTime() - now.getTime()) / 3_600_000;

  if (hoursUntilDeparture > 24)
    throw new AppError(
      "Check-in opens 24 hours before departure.",
      HTTP_STATUS.BAD_REQUEST,
    );
  if (hoursUntilDeparture <= 0)
    throw new AppError(
      "Check-in has closed for this flight.",
      HTTP_STATUS.BAD_REQUEST,
    );

  // Validate passenger belongs to this booking
  const passenger = await prisma.passenger.findFirst({
    where: { id: passengerId, bookingId },
  });

  if (!passenger)
    throw new AppError(
      "Passenger not found in this booking.",
      HTTP_STATUS.NOT_FOUND,
    );

  // Idempotency: return existing check-in if already initiated
  const existing = await prisma.checkIn.findUnique({
    where: { passengerId },
  });
  if (existing) {
    if (existing.status === "COMPLETED")
      throw new AppError(
        "Passenger has already checked in.",
        HTTP_STATUS.CONFLICT,
      );
    return existing;
  }

  // Create check-in record & mark booking as open
  const [checkin] = await prisma.$transaction([
    prisma.checkIn.create({
      data: {
        bookingId,
        passengerId,
        status: "IN_PROGRESS",
        currentStep: "PASSPORT_SCAN",
      },
    }),
    prisma.booking.update({
      where: { id: bookingId },
      data: { status: "CHECKIN_OPEN" },
    }),
  ]);

  return checkin;
}

// ─── Step 1: Passport Scan ──────────────────────────────────────────────────

export async function submitPassport(
  checkinId: string,
  data: PassportScanInput,
) {
  const checkin = await prisma.checkIn.findUnique({
    where: { id: checkinId },
    include: { passenger: true },
  });

  if (!checkin)
    throw new AppError("Check-in not found.", HTTP_STATUS.NOT_FOUND);
  assertStep(checkin.currentStep, "PASSPORT_SCAN", "passport submission");

  // Update passenger with passport data
  await prisma.passenger.update({
    where: { id: checkin.passengerId },
    data: {
      passportNumber: data.passportNumber,
      passportExpiry: new Date(data.passportExpiry),
      passportMrz: data.passportMrz,
      passportScanUrl: data.passportScanUrl,
    },
  });

  // Advance step
  return prisma.checkIn.update({
    where: { id: checkinId },
    data: { currentStep: "DETAILS_REVIEW" },
  });
}

// ─── Step 2: Confirm Details ────────────────────────────────────────────────

export async function confirmDetails(checkinId: string) {
  const checkin = await prisma.checkIn.findUnique({
    where: { id: checkinId },
    include: {
      passenger: true,
      booking: { include: { flight: true } },
    },
  });

  if (!checkin)
    throw new AppError("Check-in not found.", HTTP_STATUS.NOT_FOUND);
  assertStep(checkin.currentStep, "DETAILS_REVIEW", "details confirmation");

  await prisma.checkIn.update({
    where: { id: checkinId },
    data: { currentStep: "SEAT_SELECTION" },
  });

  // Return the data the passenger just confirmed
  return {
    passenger: checkin.passenger,
    flight: checkin.booking.flight,
  };
}

// ─── Step 3a: Get Seat Map ──────────────────────────────────────────────────

export async function getSeatMap(checkinId: string) {
  const checkin = await prisma.checkIn.findUnique({
    where: { id: checkinId },
    include: { booking: true },
  });

  if (!checkin)
    throw new AppError("Check-in not found.", HTTP_STATUS.NOT_FOUND);

  const seats = await prisma.seat.findMany({
    where: { flightId: checkin.booking.flightId },
    orderBy: [{ rowNumber: "asc" }, { columnLetter: "asc" }],
    select: {
      id: true,
      seatCode: true,
      rowNumber: true,
      columnLetter: true,
      class: true,
      type: true,
      reservedByCheckinId: true, // non-null means occupied
    },
  });

  return seats.map((s) => ({
    ...s,
    isOccupied: s.reservedByCheckinId !== null,
    reservedByCheckinId: undefined, // don't leak who reserved it
  }));
}

// ─── Step 3b: Select Seat ───────────────────────────────────────────────────

export async function selectSeat(checkinId: string, data: SeatSelectionInput) {
  const checkin = await prisma.checkIn.findUnique({
    where: { id: checkinId },
  });

  if (!checkin)
    throw new AppError("Check-in not found.", HTTP_STATUS.NOT_FOUND);
  assertStep(checkin.currentStep, "SEAT_SELECTION", "seat selection");

  // Atomic reservation — prevents double-booking
  const seat = await prisma.$transaction(async (tx) => {
    const seat = await tx.seat.findUnique({ where: { id: data.seatId } });

    if (!seat) throw new AppError("Seat not found.", HTTP_STATUS.NOT_FOUND);
    if (seat.reservedByCheckinId && seat.reservedByCheckinId !== checkinId)
      throw new AppError("Seat is already taken.", HTTP_STATUS.CONFLICT);

    return tx.seat.update({
      where: { id: data.seatId },
      data: {
        reservedByCheckinId: checkinId,
        reservedAt: new Date(),
      },
    });
  });

  await prisma.checkIn.update({
    where: { id: checkinId },
    data: { currentStep: "BAGGAGE_DECLARATION" },
  });

  return seat;
}

// ─── Step 4: Baggage Declaration ────────────────────────────────────────────

export async function declareBaggage(checkinId: string, data: BaggageInput) {
  const checkin = await prisma.checkIn.findUnique({ where: { id: checkinId } });
  if (!checkin)
    throw new AppError("Check-in not found.", HTTP_STATUS.NOT_FOUND);
  assertStep(checkin.currentStep, "BAGGAGE_DECLARATION", "baggage declaration");

  // Replace any previously declared baggage (idempotent re-submission)
  await prisma.baggage.deleteMany({ where: { checkinId } });

  const bags = await prisma.baggage.createMany({
    data: data.bags.map((b) => ({
      checkinId,
      bagType: b.bagType,
      quantity: b.quantity,
      weightKg: b.weightKg,
    })),
  });

  await prisma.checkIn.update({
    where: { id: checkinId },
    data: { currentStep: "SPECIAL_REQUESTS" },
  });

  return bags;
}

// ─── Step 5: Special Requests ───────────────────────────────────────────────

export async function submitSpecialRequests(
  checkinId: string,
  data: SpecialRequestsInput,
) {
  const checkin = await prisma.checkIn.findUnique({ where: { id: checkinId } });
  if (!checkin)
    throw new AppError("Check-in not found.", HTTP_STATUS.NOT_FOUND);
  assertStep(checkin.currentStep, "SPECIAL_REQUESTS", "special requests");

  await prisma.specialRequest.deleteMany({ where: { checkinId } });

  if (data.requests.length > 0) {
    await prisma.specialRequest.createMany({
      data: data.requests.map((r) => ({
        checkinId,
        category: r.category,
        detail: r.detail,
        notes: r.notes,
      })),
    });
  }

  await prisma.checkIn.update({
    where: { id: checkinId },
    data: { currentStep: "CONFIRMATION" },
  });

  return { message: "Special requests saved." };
}

// ─── Step 6: Confirm & Generate Boarding Pass ───────────────────────────────

export async function confirmCheckIn(checkinId: string) {
  const checkin = await prisma.checkIn.findUnique({
    where: { id: checkinId },
    include: {
      passenger: true,
      booking: { include: { flight: true } },
      seatReserved: true,
      boardingPass: true, // Check if boarding pass already exists
    },
  });

  if (!checkin)
    throw new AppError("Check-in not found.", HTTP_STATUS.NOT_FOUND);

  // 2. CHECK IF IT IS ALREADY COMPLETED OR IF A PASS EXISTS
  if (checkin.status === "COMPLETED" || checkin.boardingPass) {
    throw new AppError(
      "Check-in has already been completed and a boarding pass issued.",
      HTTP_STATUS.CONFLICT, // 409 Conflict is better than 500
    );
  }
  assertStep(checkin.currentStep, "CONFIRMATION", "check-in confirmation");

  if (!checkin.seatReserved)
    throw new AppError(
      "No seat reserved. Complete seat selection first.",
      HTTP_STATUS.BAD_REQUEST,
    );

  const flight = checkin.booking.flight;
  const passenger = checkin.passenger;
  const seat = checkin.seatReserved;

  // Generate QR token
  const qrCodeData = generateBoardingPassToken({
    checkinId,
    passengerId: passenger.id,
    flightNumber: flight.flightNumber,
    seatCode: seat.seatCode,
    issuedAt: Date.now(),
  });

  const expiresAt = new Date(flight.departureAt.getTime() + 2 * 3_600_000); // departure + 2h

  // Offline payload — everything the mobile app needs without a network call
  const offlinePayload = {
    passenger: {
      firstName: passenger.firstName,
      lastName: passenger.lastName,
      passportNumber: passenger.passportNumber,
    },
    flight: {
      flightNumber: flight.flightNumber,
      origin: flight.originIata,
      destination: flight.destIata,
      departureAt: flight.departureAt,
      arrivalAt: flight.arrivalAt,
    },
    seat: {
      seatCode: seat.seatCode,
      class: seat.class,
    },
  };

  // 2. Generate and upload assets OUTSIDE the transaction to avoid locking the DB
  const qrCodeUrl = await generateAndUploadQRCode(qrCodeData, checkinId);
  // const pdfUrl = await generateAndUploadPDF(offlinePayload, checkinId, qrCodeUrl);

  // 3. Database Transaction
  const boardingPass = await prisma.$transaction(async (tx) => {
    const bp = await tx.boardingPass.create({
      data: {
        checkinId,
        passengerId: passenger.id,
        seatId: seat.id,
        qrCodeData,
        qrCodeUrl, // <-- Added
        // pdfUrl,
        expiresAt,
        offlinePayload,
      },
    });

    // Update CheckIn Status
    await tx.checkIn.update({
      where: { id: checkinId },
      data: {
        status: "COMPLETED",
        completedAt: new Date(),
      },
    });

    // Update Booking Status
    await tx.booking.update({
      where: { id: checkin.bookingId },
      data: { status: "CHECKED_IN" },
    });

    // 4. Create Notification (Only if the booking is linked to a User)
    if (checkin.booking.userId) {
      await tx.notification.create({
        data: {
          userId: checkin.booking.userId,
          bookingId: checkin.booking.id,
          type: "CHECKIN_CONFIRMED",
          channel: "PUSH",
          status: "PENDING", // Ready to be picked up by a background worker
          payload: {
            title: "Check-in Complete! ✈️",
            body: `Your boarding pass for flight ${flight.flightNumber} to ${flight.destIata} is ready.`,
            deep_link: `app://boarding-pass/${bp.id}`, // For mobile app routing
          },
        },
      });
    }

    return bp;
  });

  return boardingPass;
}

export async function getBoardingPass(checkinId: string) {
  const boardingPass = await prisma.boardingPass.findUnique({
    where: { checkinId },
    // Optionally include passenger/seat info if you need it directly from the relation,
    // though the offlinePayload already has a nice summary!
  });

  if (!boardingPass) {
    throw new AppError(
      "Boarding pass not found for this check-in.",
      HTTP_STATUS.NOT_FOUND,
    );
  }

  return boardingPass;
}
