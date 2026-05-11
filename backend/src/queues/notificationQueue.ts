import { Queue } from "bullmq";
import bullmqRedis from "../config/bullmqRedis.js";

export interface NotificationJobData {
  notificationId: string;
  userId: string;
  payload: {
    title: string;
    body: string;
    deep_link?: string;
  };
}

export const notificationQueue = new Queue<NotificationJobData>("notifications", {
  connection: bullmqRedis,
  defaultJobOptions: {
    attempts: 3,
    backoff: { type: "exponential", delay: 5000 },
    removeOnComplete: true,
    removeOnFail: false,
  },
});

export async function enqueueNotification(data: NotificationJobData): Promise<void> {
  await notificationQueue.add("send-push", data);
}