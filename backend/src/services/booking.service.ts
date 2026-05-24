import { NOT_FOUND } from "../constants/http";
import { prisma } from "../lib/prisma";
import { LookupInput } from "../schemas/booking.schema";
import AppError from "../utils/AppError";

export const lookupBooking = async (data: LookupInput) => {
  const { bookingReference, lastName } = data;
  const booking = await prisma.booking.findFirst({
    where: {
      bookingReference: {
        equals: bookingReference,
        mode: "insensitive",
      },
      lastName: {
        equals: lastName,
        mode: "insensitive",
      },
    },
    include: {
      flight: true,
      passengers: {
        include: {
          checkin: true,
          boardingPass: true,
        },
      },
    },
  });

  if (!booking) {
    throw new AppError(
      "Booking not found. Please check your reference and last name.",
      NOT_FOUND,
    );
  }

  const now = new Date();
  const departureAt = booking.flight.departureAt;
  const diffHours = (departureAt.getTime() - now.getTime()) / (1000 * 60 * 60);
  const isCheckinOpen = diffHours <= 24 && diffHours > 0;

  return {
    bookingId: booking.id,
    bookingReference: booking.bookingReference,
    status: booking.status,
    isCheckinOpen,
    flight: {
      flightNumber: booking.flight.flightNumber,
      airlineCode: booking.flight.airlineCode,
      originIata: booking.flight.originIata,
      destIata: booking.flight.destIata,
      departureAt: booking.flight.departureAt.toISOString(),
      arrivalAt: booking.flight.arrivalAt.toISOString(),
      aircraftType: booking.flight.aircraftType,
      status: booking.flight.status,
    },
    passengers: booking.passengers.map((passenger) => ({
      id: passenger.id,
      firstName: passenger.firstName,
      lastName: passenger.lastName,
      passengerType: passenger.passengerType,
      isPrimary: passenger.isPrimary,
      checkinStatus: passenger.checkin?.status ?? null,
      currentStep: passenger.checkin?.currentStep ?? null,
      hasBoardingPass: !!passenger.boardingPass,
    })),
  };
};
