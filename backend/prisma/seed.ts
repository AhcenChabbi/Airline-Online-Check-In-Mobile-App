import {
  BagStatus,
  BagType,
  BookingStatus,
  CheckInStatus,
  CheckInStep,
  FlightStatus,
  NotificationChannel,
  NotificationStatus,
  NotificationType,
  PassengerType,
  PrismaClient,
  RequestCategory,
  SeatClass,
  SeatType,
} from "../generated/prisma/client";
import { prisma } from "../src/lib/prisma";
import bcrypt from "bcrypt";

const seed = async () => {
  await prisma.$transaction([
    prisma.boardingPass.deleteMany(),
    prisma.specialRequest.deleteMany(),
    prisma.baggage.deleteMany(),
    prisma.checkIn.deleteMany(),
    prisma.passenger.deleteMany(),
    prisma.notification.deleteMany(),
    prisma.booking.deleteMany(),
    prisma.seat.deleteMany(),
    prisma.flight.deleteMany(),
    prisma.user.deleteMany(),
  ]);

  const user = await prisma.user.create({
    data: {
      fullName: "Amina Benali",
      email: "amina.benali@example.com",
      phone: "+213555123456",
      passwordHash: await bcrypt.hash("password123", 12),
      avatarUrl: "https://robohash.org/amina.png?size=200x200",
    },
  });

  const now = new Date();
  const departureAt = new Date(now.getTime() + 6 * 60 * 60 * 1000);
  const arrivalAt = new Date(now.getTime() + 9 * 60 * 60 * 1000);

  const flight = await prisma.flight.create({
    data: {
      flightNumber: "AF1234",
      airlineCode: "AF",
      originIata: "CDG",
      destIata: "ALG",
      departureAt,
      arrivalAt,
      aircraftType: "A320",
      totalRows: 30,
      seatsPerRow: 6,
      status: FlightStatus.SCHEDULED,
    },
  });

  const seats = await prisma.seat.createMany({
    data: [
      {
        flightId: flight.id,
        seatCode: "12A",
        rowNumber: 12,
        columnLetter: "A",
        class: SeatClass.ECONOMY,
        type: SeatType.WINDOW,
      },
      {
        flightId: flight.id,
        seatCode: "12B",
        rowNumber: 12,
        columnLetter: "B",
        class: SeatClass.ECONOMY,
        type: SeatType.MIDDLE,
      },
    ],
  });

  const booking = await prisma.booking.create({
    data: {
      userId: user.id,
      flightId: flight.id,
      bookingReference: "ABC123",
      lastName: "Benali",
      status: BookingStatus.CHECKIN_OPEN,
    },
  });

  const [primaryPassenger, companionPassenger] = await prisma.$transaction([
    prisma.passenger.create({
      data: {
        bookingId: booking.id,
        firstName: "Amina",
        lastName: "Benali",
        passengerType: PassengerType.ADULT,
        dateOfBirth: new Date("1992-04-12"),
        nationality: "DZ",
        passportNumber: "DZ123456",
        passportExpiry: new Date("2029-04-12"),
        isPrimary: true,
      },
    }),
    prisma.passenger.create({
      data: {
        bookingId: booking.id,
        firstName: "Youssef",
        lastName: "Benali",
        passengerType: PassengerType.CHILD,
        dateOfBirth: new Date("2015-09-30"),
        nationality: "DZ",
        passportNumber: "DZ654321",
        passportExpiry: new Date("2029-09-30"),
        isPrimary: false,
      },
    }),
  ]);

  const checkin = await prisma.checkIn.create({
    data: {
      bookingId: booking.id,
      passengerId: primaryPassenger.id,
      status: CheckInStatus.IN_PROGRESS,
      currentStep: CheckInStep.PASSPORT_SCAN,
      ipAddress: "192.168.1.12",
    },
  });

    // Seat selection and boarding pass generation happen during check-in flow

  await prisma.baggage.create({
    data: {
      checkinId: checkin.id,
      bagType: BagType.CHECKED,
      quantity: 1,
      weightKg: 18.5,
      status: BagStatus.DECLARED,
      tagNumber: "ALG001234",
    },
  });

  await prisma.specialRequest.create({
    data: {
      checkinId: checkin.id,
      category: RequestCategory.DIETARY,
      detail: "VGML",
      notes: "Vegetarian meal",
    },
  });

  await prisma.notification.create({
    data: {
      userId: user.id,
      bookingId: booking.id,
      type: NotificationType.REMINDER,
      channel: NotificationChannel.PUSH,
      status: NotificationStatus.SENT,
      sentAt: new Date(),
      payload: {
        title: "Check-in is open",
        body: "Complete your check-in to secure your seat.",
        deep_link: "app://checkin/booking/ABC123",
      },
    },
  });

  console.log("Seed data created:", {
    userId: user.id,
    bookingReference: booking.bookingReference,
    flightNumber: flight.flightNumber,
  });
};

seed()
  .catch((error) => {
    console.error("Seed failed:", error);
    process.exitCode = 1;
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
