import admin from "firebase-admin";
import { existsSync, readFileSync } from "fs";
import { resolve } from "path";
import { FCM_SERVICE_ACCOUNT_PATH, NODE_ENV } from "../config/env.js";

// Initialize only once
let fcmReady = false;

if (!admin.apps.length) {
  const resolvedServiceAccountPath = resolve(FCM_SERVICE_ACCOUNT_PATH);
  if (existsSync(resolvedServiceAccountPath)) {
    const serviceAccount = JSON.parse(
      readFileSync(resolvedServiceAccountPath, "utf-8")
    );

    admin.initializeApp({
      credential: admin.credential.cert(serviceAccount),
    });
    fcmReady = true;
    console.log("[FCM] Firebase Admin initialized.");
  } else {
    const message = `[FCM] Service account file not found at ${resolvedServiceAccountPath}.`;
    if (NODE_ENV === "production") {
      throw new Error(message);
    }
    console.warn(`${message} Push notifications are disabled.`);
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