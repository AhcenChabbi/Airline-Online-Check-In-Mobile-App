import jwt from "jsonwebtoken";
import { JWT_SECRET } from "../config/env";


export interface BoardingPassPayload {
  checkinId: string;
  passengerId: string;
  flightNumber: string;
  seatCode: string;
  issuedAt: number;
}

export function generateBoardingPassToken(payload: BoardingPassPayload): string {
  return jwt.sign(payload, JWT_SECRET, { expiresIn: "48h" });
} 