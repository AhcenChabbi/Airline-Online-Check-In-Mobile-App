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
import {
  initiateCheckInSchema,
  passportScanSchema,
  seatSelectionSchema,
  baggageSchema,
  specialRequestsSchema,
} from "../schemas/checkin.schema";
import { z } from "zod";

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
registry.register("InitiateCheckInInput", initiateCheckInSchema);
registry.register("PassportScanInput", passportScanSchema);
registry.register("SeatSelectionInput", seatSelectionSchema);
registry.register("BaggageInput", baggageSchema);
registry.register("SpecialRequestsInput", specialRequestsSchema);

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

registry.registerPath({
  method: "post",
  path: "/api/checkin/initiate",
  tags: ["Check-In"],
  security: [{ bearerAuth: [] }],
  request: {
    body: {
      required: true,
      content: { "application/json": { schema: initiateCheckInSchema } },
    },
  },
  responses: {
    201: { description: "Check-in initiated, returns checkin record" },
  },
});

registry.registerPath({
  method: "post",
  path: "/api/checkin/{checkinId}/passport",
  tags: ["Check-In"],
  security: [{ bearerAuth: [] }],
  request: {
    params: z.object({ checkinId: z.string().uuid() }),
    body: {
      required: true,
      content: { "application/json": { schema: passportScanSchema } },
    },
  },
  responses: {
    200: { description: "Passport submitted, step advanced to DETAILS_REVIEW" },
  },
});

registry.registerPath({
  method: "post",
  path: "/api/checkin/{checkinId}/details/confirm",
  tags: ["Check-In"],
  security: [{ bearerAuth: [] }],
  request: {
    params: z.object({ checkinId: z.string().uuid() }),
  },
  responses: {
    200: { description: "Details confirmed, step advanced to SEAT_SELECTION" },
  },
});

registry.registerPath({
  method: "get",
  path: "/api/checkin/{checkinId}/seats",
  tags: ["Check-In"],
  security: [{ bearerAuth: [] }],
  request: {
    params: z.object({ checkinId: z.string().uuid() }),
  },
  responses: {
    200: { description: "Seat map for the flight" },
  },
});

registry.registerPath({
  method: "post",
  path: "/api/checkin/{checkinId}/seats/select",
  tags: ["Check-In"],
  security: [{ bearerAuth: [] }],
  request: {
    params: z.object({ checkinId: z.string().uuid() }),
    body: {
      required: true,
      content: { "application/json": { schema: seatSelectionSchema } },
    },
  },
  responses: {
    200: { description: "Seat reserved, step advanced to BAGGAGE_DECLARATION" },
  },
});

registry.registerPath({
  method: "post",
  path: "/api/checkin/{checkinId}/baggage",
  tags: ["Check-In"],
  security: [{ bearerAuth: [] }],
  request: {
    params: z.object({ checkinId: z.string().uuid() }),
    body: {
      required: true,
      content: { "application/json": { schema: baggageSchema } },
    },
  },
  responses: {
    200: { description: "Baggage declared, step advanced to SPECIAL_REQUESTS" },
  },
});

registry.registerPath({
  method: "post",
  path: "/api/checkin/{checkinId}/special-requests",
  tags: ["Check-In"],
  security: [{ bearerAuth: [] }],
  request: {
    params: z.object({ checkinId: z.string().uuid() }),
    body: {
      required: true,
      content: { "application/json": { schema: specialRequestsSchema } },
    },
  },
  responses: {
    200: {
      description: "Special requests saved, step advanced to CONFIRMATION",
    },
  },
});

registry.registerPath({
  method: "post",
  path: "/api/checkin/{checkinId}/confirm",
  tags: ["Check-In"],
  security: [{ bearerAuth: [] }],
  request: {
    params: z.object({ checkinId: z.string().uuid() }),
  },
  responses: {
    201: { description: "Check-in completed, boarding pass issued" },
  },
});

registry.registerPath({
  method: "get",
  path: "/api/checkin/{checkinId}/boarding-pass",
  tags: ["Check-In"],
  security: [{ bearerAuth: [] }],
  request: {
    params: z.object({ checkinId: z.string().uuid() }),
  },
  responses: {
    200: {
      description:
        "Boarding pass data including offline payload and QR code URL",
    },
  },
});

registry.registerPath({
  method: "get",
  path: "/api/checkin/{checkinId}/boarding-pass/pdf",
  tags: ["Check-In"],
  security: [{ bearerAuth: [] }],
  request: {
    params: z.object({ checkinId: z.string().uuid() }),
  },
  responses: {
    200: {
      description: "PDF boarding pass download",
      content: {
        "application/pdf": { schema: { type: "string", format: "binary" } },
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
