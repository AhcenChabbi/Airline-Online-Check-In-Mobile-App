# Airline Online Check-In Backend

Backend service for the Airline Online Check-In mobile app.

This repository currently provides:

- Core Express server bootstrap
- Global middleware (CORS, rate limiting, logging, cookie parsing)
- Centralized error handling helpers
- Prisma data model + initial migration for the check-in domain
- Postgres local development environment via Docker Compose

## Tech Stack

- Runtime: Node.js + TypeScript (ES modules)
- API server: Express 5
- Database: PostgreSQL
- ORM: Prisma 7 (custom output to `generated/prisma`)
- Validation: Zod
- Security and utility libs: `express-rate-limit`, `cookie-parser`, `cors`, `bcrypt`, `jsonwebtoken`

## Current Project Status

The backend is in an early foundation phase.

Implemented today:

- Server startup and `/health` endpoint
- Prisma schema covering users, bookings, flights, passengers, check-in flow, seats, baggage, boarding passes, and notifications
- Shared error utilities (`AppError`, async wrapper, global error middleware)

## Folder Structure

```text
backend/
	src/
		app.ts                  # Express app and middleware wiring
		server.ts               # HTTP listener bootstrap
		lib/
			prisma.ts             # Prisma client instance
		middleware/
			errHandler.ts         # Global error middleware
		constants/
			http.ts               # HTTP status constants + type
			appErrorCode.ts       # Domain error code enum
		utils/
			AppError.ts           # Typed app error class
			catchErrors.ts        # Async controller wrapper
		routes/                 # Empty (planned)
		controllers/            # Empty (planned)
		services/               # Empty (planned)
	prisma/
		schema.prisma           # Domain data model
		migrations/             # SQL migrations history
	generated/prisma/         # Generated Prisma client output
	docker-compose.yml        # Local Postgres service
	prisma.config.ts          # Prisma config + DATABASE_URL mapping
```

## Architecture Overview

### Runtime flow

1. `src/server.ts` loads `src/app.ts`.
2. `src/app.ts` registers middleware and exposes `GET /health`.
3. Any thrown error is handled by `src/middleware/errHandler.ts`.
4. Prisma access is centralized in `src/lib/prisma.ts`.

### Error handling pattern

- Use `catchErrors` to wrap async route handlers and forward rejected promises.
- Throw `AppError` for expected business errors with explicit HTTP status.
- Zod validation errors are transformed into structured `400` responses.
- Unknown errors return `500 internal server error`.

## Data Model (Prisma)

The schema models the full mobile check-in domain:

- Identity: `User`
- Flight inventory: `Flight`, `Seat`
- Reservation and passengers: `Booking`, `Passenger`
- Check-in lifecycle: `CheckIn`, `BoardingPass`
- Ancillary services: `Baggage`, `SpecialRequest`
- Communication: `Notification`

Domain enums include:

- `FlightStatus`, `BookingStatus`
- `CheckInStatus`, `CheckInStep`
- `SeatClass`, `SeatType`
- `BagType`, `BagStatus`
- `RequestCategory`
- `NotificationType`, `NotificationChannel`, `NotificationStatus`

## Prerequisites

- Node.js 20+
- npm 10+
- Docker + Docker Compose

## Environment Variables

Create a `.env` file in the project root:

```env
DATABASE_URL="postgresql://postgres:postgres@localhost:5432/airline_checkin"
PORT=3000
```

Used by:

- `DATABASE_URL`: Prisma config and Prisma client adapter
- `PORT`: HTTP server port (defaults to `3000`)

## Local Development Setup

1. Install dependencies:

```bash
npm install
```

2. Start PostgreSQL:

```bash
docker compose up -d
```

3. Generate Prisma client:

```bash
npx prisma generate
```

4. Apply migrations:

```bash
npx prisma migrate dev
```

5. Run the API in dev mode:

```bash
npm run dev
```

6. Check service health:

```bash
curl http://localhost:3000/health
```

Expected response:

```json
{ "message": "Server is healthy" }
```

## Available Scripts

- `npm run dev`: Start development server with file watching (`tsx watch`)
- `npm start`: Start server once (`tsx src/server.ts`)
- `npm run prestart`: Regenerate Prisma client before `start`

## Database and Prisma Notes

- Prisma client output is configured to `generated/prisma` (not default `node_modules/.prisma`).
- If you update `prisma/schema.prisma`, run:

```bash
npx prisma migrate dev --name <descriptive_change_name>
npx prisma generate
```

- Optional inspection helpers:

```bash
npx prisma migrate status
npx prisma studio
```

## API Surface (Current)

### `GET /health`

Purpose:

- Simple liveness check for local/dev deployments.

Response:

- `200 OK`
- Body: `{ "message": "Server is healthy" }`

## Team Conventions (Recommended for Next Steps)

When adding new endpoints, follow this pattern:

1. Route definition in `src/routes`
2. Controller handler in `src/controllers`
3. Business logic in `src/services`
4. Input validation with Zod
5. Wrap async handlers with `catchErrors`
6. Throw `AppError` for expected failures

This keeps transport logic, validation, and domain logic cleanly separated.

## Troubleshooting

### `PrismaClientInitializationError` or DB connection failures

- Ensure Docker Postgres is running: `docker compose ps`
- Verify `.env` `DATABASE_URL` host/port/db/user/password
- Confirm port `5432` is not already used by another local Postgres instance

### `relation does not exist` errors

- Migrations may not be applied:

```bash
npx prisma migrate dev
```

### Type errors after schema changes

- Regenerate client:

```bash
npx prisma generate
```
