/*
  Warnings:

  - You are about to drop the column `is_available` on the `seats` table. All the data in the column will be lost.

*/
-- CreateEnum
CREATE TYPE "PassengerType" AS ENUM ('ADULT', 'CHILD', 'INFANT');

-- DropIndex
DROP INDEX "check_ins_booking_id_key";

-- AlterTable
ALTER TABLE "boarding_passes" ADD COLUMN     "synced_at" TIMESTAMP(3);

-- AlterTable
ALTER TABLE "passengers" ADD COLUMN     "passenger_type" "PassengerType" NOT NULL DEFAULT 'ADULT';

-- AlterTable
ALTER TABLE "seats" DROP COLUMN "is_available";

-- CreateIndex
CREATE INDEX "bookings_booking_reference_last_name_idx" ON "bookings"("booking_reference", "last_name");
