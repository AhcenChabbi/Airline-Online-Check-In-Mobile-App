import { Request, Response } from "express";
import { CREATED, OK } from "../constants/http";
import {
	googleAuthSchema,
	loginSchema,
	registerSchema,
	refreshTokenSchema,
} from "../schemas/auth.schema";
import {
	loginWithEmail,
	loginWithGoogle,
	registerWithEmail,
	refreshTokens,
} from "../services/auth.service";
import catchErrors from "../utils/catchErrors";

export const registerWithEmailController = catchErrors(
	async (req: Request, res: Response) => {
		const data = registerSchema.parse(req.body);
		const result = await registerWithEmail(data);
		res.status(CREATED).json(result);
	},
);

export const loginWithEmailController = catchErrors(
	async (req: Request, res: Response) => {
		const data = loginSchema.parse(req.body);
		const result = await loginWithEmail(data);
		res.status(OK).json(result);
	},
);

export const loginWithGoogleController = catchErrors(
	async (req: Request, res: Response) => {
		const data = googleAuthSchema.parse(req.body);
		const result = await loginWithGoogle(data);
		res.status(OK).json(result);
	},
);

export const refreshTokenController = catchErrors(
	async (req: Request, res: Response) => {
		const data = refreshTokenSchema.parse(req.body);
		const result = await refreshTokens(data);
		res.status(OK).json(result);
	},
);
