import { z } from "zod";
import { extendZodWithOpenApi } from "@asteasolutions/zod-to-openapi";

extendZodWithOpenApi(z);

export const lookupSchema = z
  .object({
    bookingReference: z.string().trim().uppercase().min(3),
    lastName: z.string().trim().min(2),
  })
  .openapi("LookupInput");

export type LookupInput = z.infer<typeof lookupSchema>;

const flightSchema = z.object({
  flightNumber: z.string(),
  airlineCode: z.string(),
  originIata: z.string(),
  destIata: z.string(),
  departureAt: z.string().datetime(),
  arrivalAt: z.string().datetime(),
  aircraftType: z.string(),
  status: z.enum([
    "SCHEDULED",
    "BOARDING",
    "DEPARTED",
    "ARRIVED",
    "CANCELLED",
    "DELAYED",
  ]),
});

const passengerSummarySchema = z.object({
  id: z.string(),
  firstName: z.string(),
  lastName: z.string(),
  passengerType: z.enum(["ADULT", "CHILD", "INFANT"]),
  isPrimary: z.boolean(),
  checkinStatus: z
    .enum(["INITIATED", "IN_PROGRESS", "COMPLETED", "ABANDONED", "EXPIRED"])
    .nullable(),
  currentStep: z
    .enum([
      "PASSPORT_SCAN",
      "DETAILS_REVIEW",
      "SEAT_SELECTION",
      "BAGGAGE_DECLARATION",
      "SPECIAL_REQUESTS",
      "CONFIRMATION",
    ])
    .nullable(),
  hasBoardingPass: z.boolean(),
});

export const bookingLookupResponseSchema = z
  .object({
    bookingId: z.string().uuid(),
    bookingReference: z.string(),
    status: z.enum(["PENDING", "CHECKIN_OPEN", "CHECKED_IN", "CANCELLED"]),
    isCheckinOpen: z.boolean(),
    flight: flightSchema,
    passengers: z.array(passengerSummarySchema),
  })
  .openapi("BookingLookupResponse");
