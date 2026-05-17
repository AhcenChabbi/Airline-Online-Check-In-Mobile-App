import type { Request, Response } from "express";
import catchErrors from "../utils/catchErrors";
import * as HTTP_STATUS from "../constants/http";
import { registerFcmTokenSchema } from "../schemas/user.schema";
import { prisma } from "../lib/prisma";

export const registerFcmToken = catchErrors(
  async (req: Request, res: Response) => {
    const { fcmToken } = registerFcmTokenSchema.parse(req.body);

    await prisma.user.update({
      where: { id: req.user!.id },
      data: { fcmToken },
    });

    res.status(HTTP_STATUS.OK).json({ message: "FCM token registered." });
  },
);
