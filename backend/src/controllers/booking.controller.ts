import { Request, Response } from "express";
import { OK } from "../constants/http";
import { lookupSchema } from "../schemas/booking.schema";
import { lookupBooking } from "../services/booking.service";
import catchErrors from "../utils/catchErrors";

export const lookupBookingController = catchErrors(
	async (req: Request, res: Response) => {
		const data = lookupSchema.parse(req.body);
		const result = await lookupBooking(data);
		res.status(OK).json(result);
	},
);
