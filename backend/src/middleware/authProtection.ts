import { RequestHandler } from "express";
import { rateLimit, ipKeyGenerator } from "express-rate-limit";
import { TOO_MANY_REQUESTS, UNAUTHORIZED } from "../constants/http";
import AppError from "../utils/AppError";

const WINDOW_MS = 15 * 60 * 1000;
const MAX_FAILURES = 5;
const BLOCK_MS = 15 * 60 * 1000;

type AttemptState = {
  count: number;
  lastFailedAt: number;
  blockedUntil: number;
};

const attempts = new Map<string, AttemptState>();

const getAttemptKey = (req: Parameters<RequestHandler>[0]) => {
  const email =
    typeof req.body?.email === "string" ? req.body.email.toLowerCase() : "";
  const ip = ipKeyGenerator(req.ip!);
  return email ? `${ip}:${email}` : ip;
};

export const authRateLimiter = rateLimit({
  windowMs: WINDOW_MS,
  max: 30,
  standardHeaders: true,
  legacyHeaders: false,
  message: "Too many authentication requests, please try again later.",
  keyGenerator: (req) => {
    const email =
      typeof req.body?.email === "string" ? req.body.email.toLowerCase() : "";
    const ip = ipKeyGenerator(req.ip!);
    return email ? `${ip}:${email}` : ip;
  },
});

export const bruteForceProtection: RequestHandler = (req, res, next) => {
  const key = getAttemptKey(req);
  const now = Date.now();
  const current = attempts.get(key);

  if (current && current.blockedUntil > now) {
    return next(
      new AppError(
        "Too many failed attempts, please try again later.",
        TOO_MANY_REQUESTS,
      ),
    );
  }

  res.on("finish", () => {
    if (res.statusCode !== UNAUTHORIZED) {
      if (res.statusCode < 400) {
        attempts.delete(key);
      }
      return;
    }

    const existing = attempts.get(key);
    const withinWindow = existing && now - existing.lastFailedAt < WINDOW_MS;
    const count = withinWindow ? existing.count + 1 : 1;

    attempts.set(key, {
      count,
      lastFailedAt: now,
      blockedUntil: count >= MAX_FAILURES ? now + BLOCK_MS : 0,
    });
  });

  return next();
};
