import { v2 as cloudinary } from "cloudinary";
import QRCode from "qrcode";
import PDFDocument from "pdfkit";
import AppError from "./AppError";
import * as HTTP_STATUS from "../constants/http";
import {
  CLOUDINARY_CLOUD_NAME,
  CLOUDINARY_API_SECRET,
  CLOUDINARY_API_KEY,
} from "./../config/env";
import axios from "axios";

// Configure Cloudinary
cloudinary.config({
  cloud_name: CLOUDINARY_CLOUD_NAME,
  api_key: CLOUDINARY_API_KEY,
  api_secret: CLOUDINARY_API_SECRET,
});

/**
 * Generates a QR Code as a Data URI and uploads it to Cloudinary
 */
export async function generateAndUploadQRCode(
  qrCodeData: string,
  checkinId: string,
): Promise<string> {
  try {
    // 1. Generate QR code as a Base64 string (Data URI)
    const qrCodeBase64 = await QRCode.toDataURL(qrCodeData);

    // 2. Upload directly to Cloudinary
    const uploadResult = await cloudinary.uploader.upload(qrCodeBase64, {
      folder: "boarding_passes/qr_codes",
      public_id: `qr_${checkinId}`, // Names the file cleanly
      format: "png",
    });

    // 3. Return the secure HTTPS URL
    return uploadResult.secure_url;
  } catch (error) {
    console.error("QR Upload Error:", error);
    throw new AppError(
      "Failed to generate QR Code asset.",
      HTTP_STATUS.INTERNAL_SERVER_ERROR,
    );
  }
}

/**
 * Placeholder for PDF generation
 */
export async function generateBoardingPassPdfBuffer(
  offlinePayload: any,
  qrCodeUrl: string,
): Promise<Buffer> {
  const response = await axios.get(qrCodeUrl, { responseType: "arraybuffer" });
  const qrBuffer = Buffer.from(response.data);

  return new Promise((resolve, reject) => {
    const doc = new PDFDocument({ margin: 50, size: "A4" });
    const chunks: Buffer[] = [];

    doc.on("data", (chunk) => chunks.push(chunk));
    doc.on("end", () => resolve(Buffer.concat(chunks)));
    doc.on("error", reject);

    doc
      .fontSize(24)
      .font("Helvetica-Bold")
      .text("BOARDING PASS", { align: "center" });
    doc.moveDown();
    doc.image(qrBuffer, { fit: [150, 150], align: "center" });
    doc.moveDown(10);

    doc.fontSize(14).font("Helvetica-Bold").text("PASSENGER DETAILS");
    doc
      .fontSize(12)
      .font("Helvetica")
      .text(
        `Name: ${offlinePayload.passenger.firstName} ${offlinePayload.passenger.lastName}`,
      )
      .text(`Passport: ${offlinePayload.passenger.passportNumber}`);
    doc.moveDown();

    doc.fontSize(14).font("Helvetica-Bold").text("FLIGHT DETAILS");
    doc
      .fontSize(12)
      .font("Helvetica")
      .text(`Flight Number: ${offlinePayload.flight.flightNumber}`)
      .text(
        `Route: ${offlinePayload.flight.origin} to ${offlinePayload.flight.destination}`,
      )
      .text(
        `Departure: ${new Date(offlinePayload.flight.departureAt).toLocaleString()}`,
      );
    doc.moveDown();

    doc.fontSize(14).font("Helvetica-Bold").text("SEAT & BOARDING");
    doc
      .fontSize(12)
      .font("Helvetica")
      .text(`Seat: ${offlinePayload.seat.seatCode}`)
      .text(`Class: ${offlinePayload.seat.class}`);
    doc.moveDown(3);

    doc
      .fontSize(10)
      .font("Helvetica-Oblique")
      .text("Please present this document at the gate.", { align: "center" });

    doc.end();
  });
}
