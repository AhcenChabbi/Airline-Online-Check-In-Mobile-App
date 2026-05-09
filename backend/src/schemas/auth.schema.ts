import { z } from "zod";
import { extendZodWithOpenApi } from "@asteasolutions/zod-to-openapi";
import userSchema from "./user.schema";

extendZodWithOpenApi(z);

const passwordSchema = z
  .string()
  .min(8, "Password must be at least 8 characters")
  .regex(/[A-Z]/, "Password must contain at least one uppercase letter")
  .regex(/\d/, "Password must contain at least one digit");

export const registerSchema = z
  .object({
    fullName: z.string().min(2, "Full name must be at least 2 characters"),
    email: z.string().email("Invalid email"),
    phone: z.string().optional(),
    password: passwordSchema,
  })
  .openapi("RegisterInput");

export type RegisterInput = z.infer<typeof registerSchema>;

export const loginSchema = z
  .object({
    email: z.string().email("Invalid email"),
    password: z.string().min(1, "Password is required"),
  })
  .openapi("LoginInput");

export type LoginInput = z.infer<typeof loginSchema>;

export const googleAuthSchema = z
  .object({
    idToken: z.string().min(1, "idToken is required"),
  })
  .openapi("GoogleAuthInput");

export type GoogleAuthInput = z.infer<typeof googleAuthSchema>;

export const refreshTokenSchema = z
  .object({
    refreshToken: z.string().min(1, "Refresh token is required"),
  })
  .openapi("RefreshTokenInput");

export type RefreshTokenInput = z.infer<typeof refreshTokenSchema>;

export const authResponseSchema = z
  .object({
    accessToken: z.string(),
    refreshToken: z.string(),
    user: userSchema,
  })
  .openapi("AuthResponse");
