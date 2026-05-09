import { z } from "zod";

export const initiateCheckInSchema = z.object({
  bookingId: z.string().uuid(),
  passengerId: z.string().uuid(),
});

export const passportScanSchema = z.object({
  passportNumber: z.string().min(5).max(20),
  passportExpiry: z.string().regex(/^\d{4}-\d{2}-\d{2}$/, "Format: YYYY-MM-DD"),
  passportMrz: z.string().optional(),
  passportScanUrl: z.string().url().optional(),
});

export const seatSelectionSchema = z.object({
  seatId: z.string().uuid(),
});

export const baggageSchema = z.object({
  bags: z
    .array(
      z.object({
        bagType: z.enum(["CARRY_ON", "CHECKED", "OVERSIZED", "FRAGILE", "SPORTS_EQUIPMENT"]),
        quantity: z.number().int().min(1).max(10),
        weightKg: z.number().positive().optional(),
      })
    )
    .min(1),
});

export const specialRequestsSchema = z.object({
  requests: z.array(
    z.object({
      category: z.enum(["DIETARY", "ACCESSIBILITY", "INFANT", "PET", "MEDICAL", "OTHER"]),
      detail: z.string().min(1).max(100),
      notes: z.string().max(500).optional(),
    })
  ),
});

export type InitiateCheckInInput = z.infer<typeof initiateCheckInSchema>;
export type PassportScanInput = z.infer<typeof passportScanSchema>;
export type SeatSelectionInput = z.infer<typeof seatSelectionSchema>;
export type BaggageInput = z.infer<typeof baggageSchema>;
export type SpecialRequestsInput = z.infer<typeof specialRequestsSchema>;