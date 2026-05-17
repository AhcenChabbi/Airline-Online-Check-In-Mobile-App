import rateLimit from "express-rate-limit";
import { RedisStore } from "rate-limit-redis";
import redisClient from "../config/redisClient";
import { TOO_MANY_REQUESTS } from "../constants/http";

/**
 * General API rate limiter: 100 requests / 15 minutes per IP
 */
export const apiLimiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  limit: 100,
  standardHeaders: "draft-7", // RateLimit headers (RFC draft 7)
  legacyHeaders: false,
  store: new RedisStore({
    sendCommand: (...args: string[]) => redisClient.sendCommand(args),
    prefix: "rl:api:",
  }),
  message: {
    status: TOO_MANY_REQUESTS,
    message: "Too many requests. Please try again later.",
  },
});

/**
 * Strict limiter for auth routes: 10 requests / 15 minutes per IP
 */
export const authLimiter = rateLimit({
  windowMs: 15 * 60 * 1000,
  limit: 10,
  standardHeaders: "draft-7",
  legacyHeaders: false,
  store: new RedisStore({
    sendCommand: (...args: string[]) => redisClient.sendCommand(args),
    prefix: "rl:auth:",
  }),
  message: {
    status: TOO_MANY_REQUESTS,
    message: "Too many login attempts. Please try again in 15 minutes.",
  },
});
