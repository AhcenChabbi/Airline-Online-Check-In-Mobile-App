import {
  OpenAPIRegistry,
  OpenApiGeneratorV3,
} from "@asteasolutions/zod-to-openapi";
import {
  registerSchema,
  loginSchema,
  googleAuthSchema,
  refreshTokenSchema,
  authResponseSchema,
} from "../schemas/auth.schema";
import { z } from "zod";
import { PORT } from "../config/env";
import userSchema from "../schemas/user.schema";

const registry = new OpenAPIRegistry();
registry.register("RegisterInput", registerSchema);
registry.register("LoginInput", loginSchema);
registry.register("GoogleAuthInput", googleAuthSchema);
registry.register("RefreshTokenInput", refreshTokenSchema);

registry.register("User", userSchema);
registry.register("AuthResponse", authResponseSchema);

registry.registerPath({
  method: "post",
  path: "/api/auth/register",
  tags: ["Auth"],
  request: {
    body: {
      description: "Register with email and password",
      required: true,
      content: {
        "application/json": {
          schema: registerSchema,
        },
      },
    },
  },
  responses: {
    201: {
      description: "User registered",
      content: {
        "application/json": {
          schema: authResponseSchema,
        },
      },
    },
  },
});

registry.registerPath({
  method: "post",
  path: "/api/auth/login",
  tags: ["Auth"],
  request: {
    body: {
      description: "Login with email and password",
      required: true,
      content: {
        "application/json": {
          schema: loginSchema,
        },
      },
    },
  },
  responses: {
    200: {
      description: "Login successful",
      content: {
        "application/json": {
          schema: authResponseSchema,
        },
      },
    },
  },
});

registry.registerPath({
  method: "post",
  path: "/api/auth/google",
  tags: ["Auth"],
  request: {
    body: {
      description: "Login with Google ID token",
      required: true,
      content: {
        "application/json": {
          schema: googleAuthSchema,
        },
      },
    },
  },
  responses: {
    200: {
      description: "Google login successful",
      content: {
        "application/json": {
          schema: authResponseSchema,
        },
      },
    },
  },
});

registry.registerPath({
  method: "post",
  path: "/api/auth/refresh",
  tags: ["Auth"],
  request: {
    body: {
      description: "Refresh access token",
      required: true,
      content: {
        "application/json": {
          schema: refreshTokenSchema,
        },
      },
    },
  },
  responses: {
    200: {
      description: "Tokens refreshed",
      content: {
        "application/json": {
          schema: authResponseSchema,
        },
      },
    },
  },
});

registry.registerPath({
  method: "get",
  path: "/api/users/me",
  tags: ["Users"],
  security: [{ bearerAuth: [] }],
  responses: {
    200: {
      description: "Current user",
      content: {
        "application/json": {
          schema: z.object({ user: userSchema }),
        },
      },
    },
  },
});

const generator = new OpenApiGeneratorV3(registry.definitions);

export const swaggerSpec = generator.generateDocument({
  openapi: "3.0.3",
  info: {
    title: "Airline Online Check-In API",
    version: "1.0.0",
    description: "Backend API for the Airline Online Check-In mobile app",
  },
  servers: [{ url: `http://localhost:${PORT}` }],
  security: [
    {
      bearerAuth: [],
    },
  ],
});
