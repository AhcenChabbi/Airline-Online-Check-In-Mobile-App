import { v2 as cloudinary } from "cloudinary";
import QRCode from "qrcode";
import PDFDocument from "pdfkit";
import AppError from "./AppError.js";
import * as HTTP_STATUS from "../constants/http.js";
import { CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_SECRET, CLOUDINARY_API_KEY } from './../config/env';

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
/**
 * Generates a PDF document in memory and streams it directly to Cloudinary
 */
export async function generateAndUploadPDF(
  offlinePayload: any, 
  checkinId: string
): Promise<string> {
  return new Promise((resolve, reject) => {
    try {
      // 1. Initialize a new PDF Document
      const doc = new PDFDocument({ margin: 50, size: "A4" });

      // 2. Set up the Cloudinary upload stream
      const uploadStream = cloudinary.uploader.upload_stream(
        {
          folder: "boarding_passes/pdfs",
          public_id: `pdf_${checkinId}`,
          format: "pdf", // Force PDF format
        },
        (error, result) => {
          if (error) {
            console.error("Cloudinary PDF Upload Error:", error);
            return reject(
              new AppError("Failed to upload PDF.", HTTP_STATUS.INTERNAL_SERVER_ERROR)
            );
          }
          // Resolve the promise with the secure URL once upload finishes
          if (result) resolve(result.secure_url);
        }
      );

      // 3. Pipe the PDF generator directly into the Cloudinary stream
      doc.pipe(uploadStream);

      // --- Draw the PDF Content ---
      
      // Header
      doc.fontSize(24).font("Helvetica-Bold").text("BOARDING PASS", { align: "center" });
      doc.moveDown(2);

      // Passenger Info
      doc.fontSize(14).font("Helvetica-Bold").text("PASSENGER DETAILS");
      doc.fontSize(12).font("Helvetica")
         .text(`Name: ${offlinePayload.passenger.firstName} ${offlinePayload.passenger.lastName}`)
         .text(`Passport: ${offlinePayload.passenger.passportNumber}`);
      doc.moveDown(1.5);

      // Flight Info
      doc.fontSize(14).font("Helvetica-Bold").text("FLIGHT DETAILS");
      doc.fontSize(12).font("Helvetica")
         .text(`Flight Number: ${offlinePayload.flight.flightNumber}`)
         .text(`Route: ${offlinePayload.flight.origin} to ${offlinePayload.flight.destination}`)
         .text(`Departure: ${new Date(offlinePayload.flight.departureAt).toLocaleString()}`);
      doc.moveDown(1.5);

      // Seat Info
      doc.fontSize(14).font("Helvetica-Bold").text("SEAT & BOARDING");
      doc.fontSize(12).font("Helvetica")
         .text(`Seat: ${offlinePayload.seat.seatCode}`)
         .text(`Class: ${offlinePayload.seat.class}`);
      
      doc.moveDown(3);
      doc.fontSize(10).font("Helvetica-Oblique").text("Please present this document at the gate.", { align: "center" });

      // 4. Finalize the PDF. This automatically ends the stream and triggers the upload.
      doc.end();

    } catch (error) {
      console.error("PDF Generation Error:", error);
      reject(new AppError("Failed to generate PDF.", HTTP_STATUS.INTERNAL_SERVER_ERROR));
    }
  });
}
