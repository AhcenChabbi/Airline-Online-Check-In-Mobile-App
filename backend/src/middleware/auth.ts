import { RequestHandler } from "express";
import jwt from "jsonwebtoken";
import { UNAUTHORIZED } from "../constants/http";
import { JWT_SECRET } from "../config/env";
import { prisma } from "../lib/prisma";
import AppError from "../utils/AppError";

export const requireAuth: RequestHandler = async (req, _res, next) => {
  const authHeader = req.headers.authorization;
  const token = authHeader?.startsWith("Bearer ")
    ? authHeader.slice("Bearer ".length)
    : undefined;

  if (!token) {
    return next(new AppError("Unauthorized", UNAUTHORIZED));
  }

  let payload: jwt.JwtPayload | null = null;

  try {
    const decoded = jwt.verify(token, JWT_SECRET);
    payload = typeof decoded === "string" ? null : decoded;
  } catch {
    return next(new AppError("Invalid or expired token", UNAUTHORIZED));
  }

  const userId = payload?.sub;

  if (!userId || typeof userId !== "string") {
    return next(new AppError("Invalid or expired token", UNAUTHORIZED));
  }

  const user = await prisma.user.findUnique({
    where: { id: userId },
  });

  if (!user) {
    return next(new AppError("Invalid or expired token", UNAUTHORIZED));
  }

  const { passwordHash: _passwordHash, ...safeUser } = user;
  req.user = safeUser;
  return next();
};
