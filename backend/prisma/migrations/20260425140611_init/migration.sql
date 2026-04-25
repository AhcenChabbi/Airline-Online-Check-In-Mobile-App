-- CreateEnum
CREATE TYPE "FlightStatus" AS ENUM ('SCHEDULED', 'BOARDING', 'DEPARTED', 'ARRIVED', 'CANCELLED', 'DELAYED');

-- CreateEnum
CREATE TYPE "BookingStatus" AS ENUM ('PENDING', 'CHECKIN_OPEN', 'CHECKED_IN', 'CANCELLED');

-- CreateEnum
CREATE TYPE "CheckInStatus" AS ENUM ('INITIATED', 'IN_PROGRESS', 'COMPLETED', 'ABANDONED', 'EXPIRED');

-- CreateEnum
CREATE TYPE "CheckInStep" AS ENUM ('PASSPORT_SCAN', 'DETAILS_REVIEW', 'SEAT_SELECTION', 'BAGGAGE_DECLARATION', 'SPECIAL_REQUESTS', 'CONFIRMATION');

-- CreateEnum
CREATE TYPE "SeatClass" AS ENUM ('ECONOMY', 'BUSINESS', 'FIRST');

-- CreateEnum
CREATE TYPE "SeatType" AS ENUM ('STANDARD', 'WINDOW', 'AISLE', 'MIDDLE', 'EXTRA_LEGROOM', 'EMERGENCY_EXIT');

-- CreateEnum
CREATE TYPE "BagType" AS ENUM ('CARRY_ON', 'CHECKED', 'OVERSIZED', 'FRAGILE', 'SPORTS_EQUIPMENT');

-- CreateEnum
CREATE TYPE "BagStatus" AS ENUM ('DECLARED', 'DROPPED', 'IN_TRANSIT', 'DELIVERED');

-- CreateEnum
CREATE TYPE "RequestCategory" AS ENUM ('DIETARY', 'ACCESSIBILITY', 'INFANT', 'PET', 'MEDICAL', 'OTHER');

-- CreateEnum
CREATE TYPE "NotificationType" AS ENUM ('CHECKIN_CONFIRMED', 'BOARDING_PASS_READY', 'GATE_CHANGE', 'FLIGHT_DELAY', 'FLIGHT_CANCELLED', 'REMINDER');

-- CreateEnum
CREATE TYPE "NotificationChannel" AS ENUM ('PUSH', 'EMAIL', 'SMS');

-- CreateEnum
CREATE TYPE "NotificationStatus" AS ENUM ('PENDING', 'SENT', 'DELIVERED', 'READ', 'FAILED');

-- CreateTable
CREATE TABLE "users" (
    "id" TEXT NOT NULL,
    "full_name" TEXT NOT NULL,
    "email" TEXT NOT NULL,
    "phone" TEXT,
    "password_hash" TEXT,
    "google_id" TEXT,
    "avatar_url" TEXT,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "users_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "flights" (
    "id" TEXT NOT NULL,
    "flight_number" TEXT NOT NULL,
    "airline_code" TEXT NOT NULL,
    "origin_iata" TEXT NOT NULL,
    "destination_iata" TEXT NOT NULL,
    "departure_at" TIMESTAMP(3) NOT NULL,
    "arrival_at" TIMESTAMP(3) NOT NULL,
    "aircraft_type" TEXT NOT NULL,
    "total_rows" INTEGER NOT NULL,
    "seats_per_row" INTEGER NOT NULL,
    "status" "FlightStatus" NOT NULL DEFAULT 'SCHEDULED',
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "flights_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "bookings" (
    "id" TEXT NOT NULL,
    "user_id" TEXT,
    "flight_id" TEXT NOT NULL,
    "booking_reference" TEXT NOT NULL,
    "last_name" TEXT NOT NULL,
    "status" "BookingStatus" NOT NULL DEFAULT 'PENDING',
    "booked_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "expires_at" TIMESTAMP(3),

    CONSTRAINT "bookings_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "passengers" (
    "id" TEXT NOT NULL,
    "booking_id" TEXT NOT NULL,
    "first_name" TEXT NOT NULL,
    "last_name" TEXT NOT NULL,
    "date_of_birth" DATE NOT NULL,
    "nationality" TEXT NOT NULL,
    "passport_number" TEXT,
    "passport_expiry" DATE,
    "passport_mrz" TEXT,
    "passport_scan_url" TEXT,
    "is_primary" BOOLEAN NOT NULL DEFAULT false,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "passengers_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "check_ins" (
    "id" TEXT NOT NULL,
    "booking_id" TEXT NOT NULL,
    "passenger_id" TEXT NOT NULL,
    "status" "CheckInStatus" NOT NULL DEFAULT 'INITIATED',
    "current_step" "CheckInStep" NOT NULL DEFAULT 'PASSPORT_SCAN',
    "started_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "completed_at" TIMESTAMP(3),
    "ip_address" TEXT,

    CONSTRAINT "check_ins_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "seats" (
    "id" TEXT NOT NULL,
    "flight_id" TEXT NOT NULL,
    "seat_code" TEXT NOT NULL,
    "row_number" INTEGER NOT NULL,
    "column_letter" TEXT NOT NULL,
    "class" "SeatClass" NOT NULL DEFAULT 'ECONOMY',
    "type" "SeatType" NOT NULL DEFAULT 'STANDARD',
    "is_available" BOOLEAN NOT NULL DEFAULT true,
    "reserved_by_checkin_id" TEXT,
    "reserved_at" TIMESTAMP(3),

    CONSTRAINT "seats_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "baggage" (
    "id" TEXT NOT NULL,
    "checkin_id" TEXT NOT NULL,
    "bag_type" "BagType" NOT NULL,
    "quantity" INTEGER NOT NULL DEFAULT 1,
    "weight_kg" DOUBLE PRECISION,
    "status" "BagStatus" NOT NULL DEFAULT 'DECLARED',
    "tag_number" TEXT,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "baggage_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "special_requests" (
    "id" TEXT NOT NULL,
    "checkin_id" TEXT NOT NULL,
    "category" "RequestCategory" NOT NULL,
    "detail" TEXT NOT NULL,
    "notes" TEXT,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "special_requests_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "boarding_passes" (
    "id" TEXT NOT NULL,
    "checkin_id" TEXT NOT NULL,
    "passenger_id" TEXT NOT NULL,
    "seat_id" TEXT NOT NULL,
    "qr_code_data" TEXT NOT NULL,
    "qr_code_url" TEXT,
    "pdf_url" TEXT,
    "is_synced" BOOLEAN NOT NULL DEFAULT false,
    "issued_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "expires_at" TIMESTAMP(3) NOT NULL,
    "offline_payload" JSONB NOT NULL,

    CONSTRAINT "boarding_passes_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "notifications" (
    "id" TEXT NOT NULL,
    "user_id" TEXT NOT NULL,
    "booking_id" TEXT,
    "type" "NotificationType" NOT NULL,
    "channel" "NotificationChannel" NOT NULL,
    "status" "NotificationStatus" NOT NULL DEFAULT 'PENDING',
    "payload" JSONB NOT NULL,
    "sent_at" TIMESTAMP(3),
    "read_at" TIMESTAMP(3),

    CONSTRAINT "notifications_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE UNIQUE INDEX "users_email_key" ON "users"("email");

-- CreateIndex
CREATE UNIQUE INDEX "users_google_id_key" ON "users"("google_id");

-- CreateIndex
CREATE UNIQUE INDEX "flights_flight_number_key" ON "flights"("flight_number");

-- CreateIndex
CREATE UNIQUE INDEX "bookings_booking_reference_key" ON "bookings"("booking_reference");

-- CreateIndex
CREATE UNIQUE INDEX "passengers_booking_id_passport_number_key" ON "passengers"("booking_id", "passport_number");

-- CreateIndex
CREATE UNIQUE INDEX "check_ins_booking_id_key" ON "check_ins"("booking_id");

-- CreateIndex
CREATE UNIQUE INDEX "check_ins_passenger_id_key" ON "check_ins"("passenger_id");

-- CreateIndex
CREATE UNIQUE INDEX "seats_reserved_by_checkin_id_key" ON "seats"("reserved_by_checkin_id");

-- CreateIndex
CREATE UNIQUE INDEX "seats_flight_id_seat_code_key" ON "seats"("flight_id", "seat_code");

-- CreateIndex
CREATE UNIQUE INDEX "baggage_tag_number_key" ON "baggage"("tag_number");

-- CreateIndex
CREATE UNIQUE INDEX "boarding_passes_checkin_id_key" ON "boarding_passes"("checkin_id");

-- CreateIndex
CREATE UNIQUE INDEX "boarding_passes_passenger_id_key" ON "boarding_passes"("passenger_id");

-- CreateIndex
CREATE UNIQUE INDEX "boarding_passes_seat_id_key" ON "boarding_passes"("seat_id");

-- CreateIndex
CREATE UNIQUE INDEX "boarding_passes_qr_code_data_key" ON "boarding_passes"("qr_code_data");

-- AddForeignKey
ALTER TABLE "bookings" ADD CONSTRAINT "bookings_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "users"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "bookings" ADD CONSTRAINT "bookings_flight_id_fkey" FOREIGN KEY ("flight_id") REFERENCES "flights"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "passengers" ADD CONSTRAINT "passengers_booking_id_fkey" FOREIGN KEY ("booking_id") REFERENCES "bookings"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "check_ins" ADD CONSTRAINT "check_ins_booking_id_fkey" FOREIGN KEY ("booking_id") REFERENCES "bookings"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "check_ins" ADD CONSTRAINT "check_ins_passenger_id_fkey" FOREIGN KEY ("passenger_id") REFERENCES "passengers"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "seats" ADD CONSTRAINT "seats_flight_id_fkey" FOREIGN KEY ("flight_id") REFERENCES "flights"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "seats" ADD CONSTRAINT "seats_reserved_by_checkin_id_fkey" FOREIGN KEY ("reserved_by_checkin_id") REFERENCES "check_ins"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "baggage" ADD CONSTRAINT "baggage_checkin_id_fkey" FOREIGN KEY ("checkin_id") REFERENCES "check_ins"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "special_requests" ADD CONSTRAINT "special_requests_checkin_id_fkey" FOREIGN KEY ("checkin_id") REFERENCES "check_ins"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "boarding_passes" ADD CONSTRAINT "boarding_passes_checkin_id_fkey" FOREIGN KEY ("checkin_id") REFERENCES "check_ins"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "boarding_passes" ADD CONSTRAINT "boarding_passes_passenger_id_fkey" FOREIGN KEY ("passenger_id") REFERENCES "passengers"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "boarding_passes" ADD CONSTRAINT "boarding_passes_seat_id_fkey" FOREIGN KEY ("seat_id") REFERENCES "seats"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "notifications" ADD CONSTRAINT "notifications_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "users"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "notifications" ADD CONSTRAINT "notifications_booking_id_fkey" FOREIGN KEY ("booking_id") REFERENCES "bookings"("id") ON DELETE SET NULL ON UPDATE CASCADE;
