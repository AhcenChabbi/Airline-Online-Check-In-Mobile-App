import { createClient } from "redis";
import { REDIS_URL } from "./env";

const redisClient = createClient({
  url: REDIS_URL,
});

redisClient.on("connect", () => {
  console.log("Connected to Redis");
});

redisClient.on("error", (err) => {
  console.error("Redis connection error:", err);
});

await redisClient.connect();

export default redisClient;
