import { z } from "zod";
import { extendZodWithOpenApi } from "@asteasolutions/zod-to-openapi";
extendZodWithOpenApi(z);
const userSchema = z.object({
  id: z.string(),
  fullName: z.string(),
  email: z.string().email(),
  phone: z.string().nullable(),
  googleId: z.string().nullable(),
  avatarUrl: z.string().nullable(),
  createdAt: z.string().datetime(),
  updatedAt: z.string().datetime(),
});

export const userMeResponseSchema = z
  .object({
    user: userSchema,
  })
  .openapi("UserMeResponse");

  export const registerFcmTokenSchema = z.object({
  fcmToken: z.string().min(1, "FCM token is required"),
}).openapi("RegisterFcmTokenInput");

export default userSchema;
