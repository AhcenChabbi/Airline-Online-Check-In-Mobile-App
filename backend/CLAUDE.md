# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Start dev server (tsx watch, auto-restarts)
npm run dev

# Start once (regenerates Prisma client first via prestart)
npm start

# Infrastructure (Postgres on 5433, Redis on 6379)
docker compose up -d

# Prisma — after any schema change
npx prisma migrate dev --name <descriptive_name>
npx prisma generate

# Inspect DB in browser
npx prisma studio

# Seed database
npx tsx prisma/seed.ts
```

No test suite is configured (`npm test` exits 1).

## Environment Variables

Required in `.env`:

```
DATABASE_URL=postgresql://postgres:postgres@localhost:5433/airline_checkin
PORT=4004
NODE_ENV=development
JWT_SECRET=
JWT_REFRESH_SECRET=
GOOGLE_CLIENT_ID=
CLOUDINARY_CLOUD_NAME=
CLOUDINARY_API_KEY=
CLOUDINARY_API_SECRET=
REDIS_URL=redis://localhost:6379
FCM_SERVICE_ACCOUNT_PATH=   # path to Firebase service account JSON
```

Note: Docker maps Postgres to port **5433** (not 5432).

## Architecture

### Request lifecycle

```
src/server.ts → app.ts (middleware + routes) → routes/ → controllers/ → services/ → lib/prisma.ts
```

- `src/server.ts` — starts the HTTP listener and the BullMQ notification worker
- `src/app.ts` — wires all middleware, routes, Swagger UI (dev only), Bull Board (dev only at `/admin/queues`)
- Prisma client is a singleton in `src/lib/prisma.ts`

### Route map

| Prefix | Auth | Notes |
|---|---|---|
| `POST /api/auth/register` | No | Email/password registration |
| `POST /api/auth/login` | No | Email/password login |
| `POST /api/auth/google` | No | Google OAuth token exchange |
| `POST /api/auth/refresh` | No | Refresh JWT tokens |
| `GET  /api/users/me` | Yes | Current user profile |
| `POST /api/users/fcm-token` | Yes | Register device push token |
| `POST /api/bookings/lookup` | Yes | Fetch booking by PNR + last name |
| `POST /api/checkin/initiate` | Yes | Start check-in for a passenger |
| `POST /api/checkin/:id/passport` | Yes | Submit passport data |
| `POST /api/checkin/:id/details/confirm` | Yes | Confirm passenger details |
| `GET  /api/checkin/:id/seats` | Yes | Fetch available seat map |
| `POST /api/checkin/:id/seats/select` | Yes | Reserve a seat |
| `POST /api/checkin/:id/baggage` | Yes | Declare baggage |
| `POST /api/checkin/:id/special-requests` | Yes | Submit special requests |
| `POST /api/checkin/:id/confirm` | Yes | Complete check-in, issue boarding pass |
| `GET  /api/checkin/:id/boarding-pass` | Yes | Get boarding pass (JSON) |
| `GET  /api/checkin/:id/boarding-pass/pdf` | Yes | Download boarding pass (PDF) |

Auth uses JWT Bearer tokens. `requireAuth` middleware validates the token and attaches `req.user` (without `passwordHash`).

### Check-in step machine

`checkin.service.ts` enforces a linear step order via `assertStep()`. The `CheckIn.currentStep` enum must match the expected step before any service function proceeds:

```
PASSPORT_SCAN → DETAILS_REVIEW → SEAT_SELECTION → BAGGAGE_DECLARATION → SPECIAL_REQUESTS → CONFIRMATION
```

`confirmCheckIn` generates the boarding pass: creates a signed JWT token (`generateBoardingPassToken`), uploads a QR code image to Cloudinary, generates a PDF via PDFKit (also uploaded to Cloudinary), and enqueues a push notification via BullMQ.

### Async notification pipeline

- `src/queues/notificationQueue.ts` — enqueues jobs to `NotificationJobData` shape via BullMQ
- `src/workers/notificationWorker.ts` — processes jobs: sends FCM push via `utils/fcm.ts`, writes `Notification` record to DB
- Bull Board dashboard: `http://localhost:4004/admin/queues` (dev only)

### Error handling

- Wrap all async controllers with `catchErrors(handler)` — forwards rejections to `next()`
- Throw `new AppError(message, httpStatusCode)` for expected business errors
- `errHandler` middleware (registered last in `app.ts`) handles `ZodError` → 400, `AppError` → its status, everything else → 500

### Validation pattern

All input schemas live in `src/schemas/` as Zod objects extended with `.openapi()` from `@asteasolutions/zod-to-openapi`. Parse in controllers using `.parse()` or `.safeParse()`; the thrown `ZodError` is caught automatically by `errHandler`.

### Prisma config

- Client output is `generated/prisma` (not the default `node_modules/.prisma`) — configured in `prisma.config.ts`
- `DATABASE_URL` is read from env and wired through `@prisma/adapter-pg`
- After any schema edit: `npx prisma migrate dev --name <name>` then `npx prisma generate`

### Dev-only endpoints

Available only when `NODE_ENV=development`:
- `/api/docs` — Swagger UI
- `/api/docs.json` — raw OpenAPI spec
- `/admin/queues` — Bull Board queue monitor
