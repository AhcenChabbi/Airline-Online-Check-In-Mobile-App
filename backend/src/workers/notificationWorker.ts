import { Worker } from "bullmq";
import bullmqRedis from "../config/bullmqRedis.js";
import { prisma } from "../lib/prisma.js";
import { sendPushNotification } from "../utils/fcm.js";
import type { NotificationJobData } from "../queues/notificationQueue.js";

export function startNotificationWorker() {
  const worker = new Worker<NotificationJobData>(
    "notifications",
    async (job) => {
      const { notificationId, userId, payload } = job.data;

      // Fetch the user's FCM token
      const user = await prisma.user.findUnique({
        where: { id: userId },
        select: { fcmToken: true },
      });

      if (!user?.fcmToken) {
        console.warn(`[NotificationWorker] No FCM token for user ${userId}. Skipping.`);
        // Mark as FAILED in DB but don't throw — no point retrying without a token
        await prisma.notification.update({
          where: { id: notificationId },
          data: { status: "FAILED" },
        });
        return;
      }

      // Send via FCM
      await sendPushNotification(user.fcmToken, payload);

      // Mark as SENT
      await prisma.notification.update({
        where: { id: notificationId },
        data: {
          status: "SENT",
          sentAt: new Date(),
        },
      });

      console.log(`[NotificationWorker] Notification ${notificationId} sent to user ${userId}`);
    },
    {
      connection: bullmqRedis,
      concurrency: 5,
    }
  );

  worker.on("failed", async (job, err) => {
    console.error(`[NotificationWorker] Job ${job?.id} failed:`, err.message);
    if (job?.data.notificationId) {
      await prisma.notification.update({
        where: { id: job.data.notificationId },
        data: { status: "FAILED" },
      });
    }
  });

  console.log("[NotificationWorker] Started.");
  return worker;
}