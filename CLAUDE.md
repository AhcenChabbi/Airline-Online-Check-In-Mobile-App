# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Repository Structure

This is a monorepo for an airline online check-in system with two independently runnable components:

| Directory | Stack | Role |
|---|---|---|
| `AERIAL/` | Kotlin · Jetpack Compose · Android | Mobile client (API 24+) |
| `backend/` | Node.js · TypeScript · Express · Prisma | REST API + async workers |

Each directory has its own `CLAUDE.md` with detailed build commands, architecture notes, and patterns. **Read the sub-CLAUDE.md for the component you are working on before making changes.**

## How the Two Components Connect

The Android app (`AERIAL/`) calls the backend (`backend/`) over HTTP. The backend defaults to port **4004**.

- In the Android emulator, `10.0.2.2` maps to the host's localhost. The real device uses the host's LAN IP.
- Allowed cleartext hosts are in `AERIAL/app/src/main/res/xml/network_security_config.xml`. Add any new dev host there.
- The Retrofit base URL is set in `AERIAL/app/src/main/java/com/airline/checkin/core/di/NetworkModule.kt`.

## Development Order

Start infrastructure before the app:

```bash
# 1. From backend/ — start Postgres + Redis
docker compose up -d

# 2. From backend/ — start API server
npm run dev          # tsx watch on port 4004

# 3. From AERIAL/ — build and run Android app
./gradlew assembleDebug
# or launch from Android Studio
```

## Check-In Flow (end-to-end)

The multi-step check-in is a coordinated state machine across both layers:

- **Backend** (`backend/src/services/checkin.service.ts`): enforces linear step order via `assertStep()`. The `CheckIn.currentStep` enum advances: `PASSPORT_SCAN → DETAILS_REVIEW → SEAT_SELECTION → BAGGAGE_DECLARATION → SPECIAL_REQUESTS → CONFIRMATION`.
- **Android** (`AERIAL/.../core/services/CheckInSessionStore.kt`): accumulates step data in a singleton session store and drives a matching UI flow through `CheckInViewModel`.

On `CONFIRMATION`, the backend generates a boarding pass JWT, uploads a QR image + PDF to Cloudinary, and enqueues an FCM push notification via BullMQ.

## Shared Domain Concepts

Both sides share the same domain vocabulary. When a field name or enum value appears in both, it must stay in sync manually (there is no code-gen contract between them):

- Booking reference (PNR) + last name → booking lookup
- `checkInId` — UUID issued by the backend on `/api/checkin/initiate`, threaded through all subsequent steps
- Seat codes, baggage types, special-request categories

## Key Constraints

- Backend requires Docker (Postgres on **5433**, Redis on **6379**). Postgres is not on the default 5432 port.
- Android minSdk is 24; do not use APIs above that without a compat check.
- The Android app uses KSP (not KAPT). After adding new annotated classes, run `./gradlew clean` if the build fails.
- No backend test suite exists. Android tests run with `./gradlew test` (unit) or `./gradlew connectedAndroidTest` (instrumented).
