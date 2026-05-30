import admin from "firebase-admin";
import { existsSync, readFileSync } from "fs";
import { FCM_SERVICE_ACCOUNT_PATH } from "../config/env.js";

// Initialize only once
let fcmReady = false;

if (!admin.apps.length) {
  if (existsSync(FCM_SERVICE_ACCOUNT_PATH)) {
    const serviceAccount = JSON.parse(
      readFileSync(FCM_SERVICE_ACCOUNT_PATH, "utf-8")
    );

    admin.initializeApp({
      credential: admin.credential.cert(serviceAccount),
    });
    fcmReady = true;
  } else {
    console.warn(
      "[FCM] Service account file not found. Push notifications are disabled."
    );
  }
}

export interface PushPayload {
  title: string;
  body: string;
  deep_link?: string;
}

export async function sendPushNotification(
  fcmToken: string,
  payload: PushPayload
): Promise<string> {
  if (!fcmReady) {
    return "fcm-disabled";
  }

  const message: admin.messaging.Message = {
    token: fcmToken,
    notification: {
      title: payload.title,
      body: payload.body,
    },
    data: {
      deep_link: payload.deep_link ?? "",
    },
    android: {
      priority: "high",
    },
  };

  return admin.messaging().send(message);
}