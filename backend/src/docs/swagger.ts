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
import { PORT } from "../config/env";
import userSchema, { userMeResponseSchema } from "../schemas/user.schema";
import { healthResponseSchema } from "../schemas/common.schema";
import {
  bookingLookupResponseSchema,
  lookupSchema,
} from "../schemas/booking.schema";

const registry = new OpenAPIRegistry();
registry.register("RegisterInput", registerSchema);
registry.register("LoginInput", loginSchema);
registry.register("GoogleAuthInput", googleAuthSchema);
registry.register("RefreshTokenInput", refreshTokenSchema);

registry.register("User", userSchema);
registry.register("AuthResponse", authResponseSchema);
registry.register("UserMeResponse", userMeResponseSchema);
registry.register("HealthResponse", healthResponseSchema);
registry.register("BookingLookupResponse", bookingLookupResponseSchema);
registry.register("LookupInput", lookupSchema);

registry.registerPath({
  method: "get",
  path: "/health",
  tags: ["Health"],
  responses: {
    200: {
      description: "Service health check",
      content: {
        "application/json": {
          schema: healthResponseSchema,
        },
      },
    },
  },
});

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
          schema: userMeResponseSchema,
        },
      },
    },
  },
});

registry.registerPath({
  method: "post",
  path: "/api/bookings/lookup",
  tags: ["Bookings"],
  request: {
    body: {
      description: "Lookup a booking by reference and last name",
      required: true,
      content: {
        "application/json": {
          schema: lookupSchema,
        },
      },
    },
  },
  responses: {
    200: {
      description: "Booking lookup result",
      content: {
        "application/json": {
          schema: bookingLookupResponseSchema,
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
