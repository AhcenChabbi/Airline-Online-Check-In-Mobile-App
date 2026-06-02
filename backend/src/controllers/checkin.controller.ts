import type { NextFunction, Request, Response } from "express";
import catchErrors from "../utils/catchErrors";
import * as HTTP_STATUS from "../constants/http";
import * as CheckInService from "../services/checkin.service";
import {
  initiateCheckInSchema,
  passportScanSchema,
  seatSelectionSchema,
  baggageSchema,
  specialRequestsSchema,
} from "../schemas/checkin.schema";
import { generateBoardingPassPdfBuffer } from "../utils/assets";

export const initiateCheckIn = catchErrors(
  async (req: Request, res: Response) => {
    const body = initiateCheckInSchema.parse(req.body);
    const checkin = await CheckInService.initiateCheckIn(
      body.bookingId,
      body.passengerId,
    );
    res.status(HTTP_STATUS.CREATED).json({ checkin });
  },
);

export const submitPassport = catchErrors(
  async (req: Request, res: Response) => {
    const body = passportScanSchema.parse(req.body);
    const checkin = await CheckInService.submitPassport(
      req.params.checkinId as string,
      body,
    );
    res.status(HTTP_STATUS.OK).json({ checkin });
  },
);

export const confirmDetails = catchErrors(
  async (req: Request, res: Response) => {
    const data = await CheckInService.confirmDetails(
      req.params.checkinId as string,
    );
    res.status(HTTP_STATUS.OK).json(data);
  },
);

export const getSeatMap = catchErrors(async (req: Request, res: Response) => {
  const seats = await CheckInService.getSeatMap(req.params.checkinId as string);
  res.status(HTTP_STATUS.OK).json({ seats });
});

export const selectSeat = catchErrors(async (req: Request, res: Response) => {
  const body = seatSelectionSchema.parse(req.body);
  const seat = await CheckInService.selectSeat(
    req.params.checkinId as string,
    body,
  );
  res.status(HTTP_STATUS.OK).json({ seat });
});

export const declareBaggage = catchErrors(
  async (req: Request, res: Response) => {
    const body = baggageSchema.parse(req.body);
    const result = await CheckInService.declareBaggage(
      req.params.checkinId as string,
      body,
    );
    res.status(HTTP_STATUS.OK).json({ result });
  },
);

export const submitSpecialRequests = catchErrors(
  async (req: Request, res: Response) => {
    const body = specialRequestsSchema.parse(req.body);
    const result = await CheckInService.submitSpecialRequests(
      req.params.checkinId as string,
      body,
    );
    res.status(HTTP_STATUS.OK).json(result);
  },
);

export const confirmCheckIn = catchErrors(
  async (req: Request, res: Response) => {
    const boardingPass = await CheckInService.confirmCheckIn(
      req.params.checkinId as string,
    );
    res.status(HTTP_STATUS.CREATED).json({ boardingPass });
  },
);

export const getBoardingPass = async (
  req: Request,
  res: Response,
  next: NextFunction,
) => {
  try {
    const { checkinId } = req.params;

    const boardingPass = await CheckInService.getBoardingPass(
      checkinId as string,
    );

    res.status(200).json({
      success: true,
      data: boardingPass,
    });
  } catch (error) {
    next(error);
  }
};

export const downloadBoardingPassPdf = catchErrors(
  async (req: Request, res: Response) => {
    const boardingPass = await CheckInService.getBoardingPass(
      req.params.checkinId as string,
    );
    const payload = boardingPass.offlinePayload as any;
    const pdfBuffer = await generateBoardingPassPdfBuffer(
      payload,
      boardingPass.qrCodeData,
      boardingPass.qrCodeUrl,
    );

    res.set({
      "Content-Type": "application/pdf",
      "Content-Disposition": `attachment; filename="boarding-pass-${req.params.checkinId}.pdf"`,
      "Content-Length": pdfBuffer.length,
    });
    res.end(pdfBuffer);
  },
);
