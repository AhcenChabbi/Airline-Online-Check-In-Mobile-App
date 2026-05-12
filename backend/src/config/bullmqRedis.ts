import Redis from "ioredis";
import { REDIS_URL } from "./env";

const bullmqRedis = new Redis(REDIS_URL, {
  maxRetriesPerRequest: null, // Required by BullMQ
});

bullmqRedis.on("connect", () => console.log("[BullMQ] Connected to Redis"));
bullmqRedis.on("error", (err) => console.error("[BullMQ] Redis error:", err));

export default bullmqRedis;
