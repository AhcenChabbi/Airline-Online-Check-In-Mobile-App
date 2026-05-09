import bcrypt from "bcrypt";
import { OAuth2Client } from "google-auth-library";
import jwt from "jsonwebtoken";
import { CONFLICT, UNAUTHORIZED } from "../constants/http";
import {
  GOOGLE_CLIENT_ID,
  JWT_REFRESH_SECRET,
  JWT_SECRET,
} from "../config/env";
import { prisma } from "../lib/prisma";
import {
  GoogleAuthInput,
  LoginInput,
  RegisterInput,
  RefreshTokenInput,
} from "../schemas/auth.schema";
import AppError from "../utils/AppError";

const SALT_ROUNDS = 12;
const googleClient = new OAuth2Client(GOOGLE_CLIENT_ID);

const toSafeUser = (user: {
  passwordHash?: string | null;
  createdAt?: Date;
  updatedAt?: Date;
  [key: string]: unknown;
}) => {
  const { passwordHash, ...safeUser } = user;
  return {
    ...safeUser,
    createdAt:
      safeUser.createdAt instanceof Date
        ? safeUser.createdAt.toISOString()
        : safeUser.createdAt,
    updatedAt:
      safeUser.updatedAt instanceof Date
        ? safeUser.updatedAt.toISOString()
        : safeUser.updatedAt,
  };
};

export const generateTokens = (userId: string) => {
  const accessToken = jwt.sign({ sub: userId }, JWT_SECRET, {
    expiresIn: "15m",
  });

  const refreshToken = jwt.sign({ sub: userId }, JWT_REFRESH_SECRET, {
    expiresIn: "7d",
  });

  return { accessToken, refreshToken };
};

export const registerWithEmail = async (data: RegisterInput) => {
  const existingUser = await prisma.user.findUnique({
    where: { email: data.email },
  });

  if (existingUser) {
    throw new AppError("Email already in use", CONFLICT);
  }

  const passwordHash = await bcrypt.hash(data.password, SALT_ROUNDS);

  const user = await prisma.user.create({
    data: {
      fullName: data.fullName,
      email: data.email,
      phone: data.phone,
      passwordHash,
    },
  });

  const { accessToken, refreshToken } = generateTokens(user.id);

  return { accessToken, refreshToken, user: toSafeUser(user) };
};

export const loginWithEmail = async (data: LoginInput) => {
  const user = await prisma.user.findUnique({
    where: { email: data.email },
  });

  if (!user || !user.passwordHash) {
    throw new AppError("Invalid credentials", UNAUTHORIZED);
  }

  const passwordMatch = await bcrypt.compare(data.password, user.passwordHash);

  if (!passwordMatch) {
    throw new AppError("Invalid credentials", UNAUTHORIZED);
  }

  const { accessToken, refreshToken } = generateTokens(user.id);

  return { accessToken, refreshToken, user: toSafeUser(user) };
};

export const loginWithGoogle = async (data: GoogleAuthInput) => {
  const ticket = await googleClient.verifyIdToken({
    idToken: data.idToken,
    audience: GOOGLE_CLIENT_ID,
  });

  const payload = ticket.getPayload();

  if (!payload?.sub || !payload.email) {
    throw new AppError("Invalid Google token", UNAUTHORIZED);
  }

  const existingByGoogleId = await prisma.user.findUnique({
    where: { googleId: payload.sub },
  });

  let user = existingByGoogleId;

  if (!user) {
    const existingByEmail = await prisma.user.findUnique({
      where: { email: payload.email },
    });

    if (existingByEmail) {
      user = await prisma.user.update({
        where: { id: existingByEmail.id },
        data: {
          googleId: payload.sub,
          avatarUrl: payload.picture || existingByEmail.avatarUrl,
        },
      });
    } else {
      user = await prisma.user.create({
        data: {
          fullName: payload.name || payload.email.split("@")[0],
          email: payload.email,
          googleId: payload.sub,
          avatarUrl: payload.picture || undefined,
        },
      });
    }
  }

  const { accessToken, refreshToken } = generateTokens(user.id);

  return { accessToken, refreshToken, user: toSafeUser(user) };
};

export const refreshTokens = async (data: RefreshTokenInput) => {
  let payload: jwt.JwtPayload | null = null;

  try {
    const decoded = jwt.verify(data.refreshToken, JWT_REFRESH_SECRET);
    payload = typeof decoded === "string" ? null : decoded;
  } catch {
    throw new AppError("Invalid refresh token", UNAUTHORIZED);
  }

  const userId = payload?.sub;

  if (!userId || typeof userId !== "string") {
    throw new AppError("Invalid refresh token", UNAUTHORIZED);
  }

  const user = await prisma.user.findUnique({
    where: { id: userId },
  });

  if (!user) {
    throw new AppError("Invalid refresh token", UNAUTHORIZED);
  }

  const { accessToken, refreshToken } = generateTokens(user.id);

  return { accessToken, refreshToken, user: toSafeUser(user) };
};
