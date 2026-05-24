package com.airline.checkin.data.local.mapper

import com.airline.checkin.data.local.room.entity.BoardingPassEntity
import com.airline.checkin.data.local.room.entity.BookingEntity
import com.airline.checkin.data.local.room.entity.BookingStatus as LocalBookingStatus
import com.airline.checkin.data.local.room.entity.FlightEntity
import com.airline.checkin.data.local.room.entity.FlightStatus as LocalFlightStatus
import com.airline.checkin.data.local.room.entity.NotificationEntity
import com.airline.checkin.data.local.room.entity.NotificationStatus as LocalNotificationStatus
import com.airline.checkin.data.local.room.entity.NotificationType as LocalNotificationType
import com.airline.checkin.data.local.room.entity.PassengerEntity
import com.airline.checkin.data.local.room.entity.PassengerType as LocalPassengerType
import com.airline.checkin.data.local.room.entity.SeatClass as LocalSeatClass
import com.airline.checkin.data.local.room.entity.SeatEntity
import com.airline.checkin.data.local.room.entity.SeatType as LocalSeatType
import com.airline.checkin.domain.model.BoardingPass
import com.airline.checkin.domain.model.BoardingPassOfflinePayload
import com.airline.checkin.domain.model.Booking
import com.airline.checkin.domain.model.Flight
import com.airline.checkin.domain.model.Notification
import com.airline.checkin.domain.model.NotificationChannel
import com.airline.checkin.domain.model.NotificationPayload
import com.airline.checkin.domain.model.NotificationStatus
import com.airline.checkin.domain.model.NotificationType
import com.airline.checkin.domain.model.Passenger
import com.airline.checkin.domain.model.Seat
import com.google.gson.Gson
import java.time.Instant
import java.util.Date

// Mapper to convert between Room entities and domain models
object EntityMapper {
    private val gson = Gson()

    fun FlightEntity.toDomain(): Flight =
            Flight(
                    id = id,
                    flightNumber = flightNumber,
                    airlineCode = airlineCode,
                    airlineName = airlineName.ifBlank { airlineCode },
                    originIata = originIata,
                    originCity = originCity.ifBlank { originIata },
                    destinationIata = destIata,
                    destinationCity = destinationCity.ifBlank { destIata },
                    departureTime = epochToIso(departureAt),
                    arrivalTime = epochToIso(arrivalAt),
                    date = epochToIso(departureAt),
                    aircraftType = aircraftType,
                    status = status.name,
                    createdAt = epochToIso(cachedAt)
            )

    fun Flight.toEntity(): FlightEntity =
            FlightEntity(
                    id = id,
                    flightNumber = flightNumber,
                    airlineCode = airlineCode,
                    airlineName = airlineName,
                    originIata = originIata,
                    originCity = originCity,
                    destIata = destinationIata,
                    destinationCity = destinationCity,
                    departureAt = parseIsoOrNow(departureTime),
                    arrivalAt = parseIsoOrNow(arrivalTime),
                    aircraftType = aircraftType,
                    status = enumOrDefault(status, LocalFlightStatus.SCHEDULED)
            )

    fun BookingEntity.toDomain(): Booking =
            Booking(
                    id = id,
                    userId = userId,
                    flightId = flightId,
                    bookingReference = bookingReference,
                    lastName = lastName,
                    status = status.name,
                    bookedAt = epochToIso(cachedAt),
                    expiresAt = null
            )

    fun Booking.toEntity(): BookingEntity =
            BookingEntity(
                    id = id,
                    userId = userId,
                    flightId = flightId,
                    bookingReference = bookingReference,
                    lastName = lastName,
                    status = enumOrDefault(status, LocalBookingStatus.PENDING)
            )

    fun PassengerEntity.toDomain(): Passenger =
            Passenger(
                    id = id,
                    bookingId = bookingId,
                    firstName = firstName,
                    lastName = lastName,
                    passengerType = passengerType.name,
                    isPrimary = isPrimary,
                    createdAt = epochToIso(cachedAt)
            )

    fun Passenger.toEntity(): PassengerEntity =
            PassengerEntity(
                    id = id,
                    bookingId = bookingId,
                    firstName = firstName,
                    lastName = lastName,
                    passengerType = enumOrDefault(passengerType, LocalPassengerType.ADULT),
                    isPrimary = isPrimary
            )

    fun SeatEntity.toDomain(): Seat =
            Seat(
                    id = id,
                    flightId = flightId,
                    seatCode = seatCode,
                    rowNumber = rowNumber,
                    columnLetter = columnLetter,
                    seatClass = seatClass.name,
                    seatType = type.name,
                    isOccupied = false
            )

    fun Seat.toEntity(): SeatEntity =
            SeatEntity(
                    id = id,
                    flightId = flightId,
                    seatCode = seatCode,
                    rowNumber = rowNumber,
                    columnLetter = columnLetter,
                    seatClass = enumOrDefault(seatClass, LocalSeatClass.ECONOMY),
                    type = enumOrDefault(seatType, LocalSeatType.STANDARD)
            )

    fun BoardingPassEntity.toDomain(): BoardingPass =
            BoardingPass(
                    id = id,
                    checkinId = checkinId,
                    passengerId = passengerId,
                    seatId = seatId,
                    qrCodeData = qrCodeData,
                    qrCodeUrl = qrCodeUrl,
                    pdfUrl = pdfUrl,
                    isSynced = isSynced,
                    syncedAt = syncedAt?.let { epochToIso(it) },
                    issuedAt = epochToIso(issuedAt),
                    expiresAt = epochToIso(expiresAt),
                    offlinePayload = parseOfflinePayload(offlinePayload)
            )

    fun BoardingPass.toEntity(): BoardingPassEntity =
            BoardingPassEntity(
                    id = id,
                    checkinId = checkinId,
                    passengerId = passengerId,
                    seatId = seatId,
                    qrCodeData = qrCodeData,
                    qrCodeUrl = qrCodeUrl,
                    pdfUrl = pdfUrl,
                    issuedAt = parseIsoOrNow(issuedAt),
                    expiresAt = parseIsoOrNow(expiresAt),
                    offlinePayload = offlinePayload?.let { gson.toJson(it) } ?: "{}",
                    isSynced = isSynced,
                    syncedAt = parseIsoOrNull(syncedAt)
            )

    fun NotificationEntity.toDomain(): Notification =
            Notification(
                    id = id,
                    userId = userId,
                    bookingId = bookingId,
                    type = NotificationType.valueOf(type.name),
                    channel = NotificationChannel.PUSH,
                    status = NotificationStatus.valueOf(status.name),
                    payload = NotificationPayload(title = title, body = body),
                    title = title,
                    body = body,
                    sentAt = Date(receivedAt),
                    readAt = if (isRead) Date(receivedAt) else null
            )

    fun Notification.toEntity(): NotificationEntity =
            NotificationEntity(
                    id = id,
                    userId = userId,
                    bookingId = bookingId ?: "",
                    type = LocalNotificationType.valueOf(type.name),
                    status = LocalNotificationStatus.valueOf(status.name),
                    title = title,
                    body = body,
                    isRead = status == NotificationStatus.READ || readAt != null,
                    receivedAt = sentAt?.time ?: System.currentTimeMillis()
            )

    private fun parseOfflinePayload(json: String): BoardingPassOfflinePayload? =
            runCatching { gson.fromJson(json, BoardingPassOfflinePayload::class.java) }.getOrNull()

    private fun epochToIso(epochMs: Long): String = Instant.ofEpochMilli(epochMs).toString()

    private fun parseIsoOrNow(value: String?): Long =
            value?.let { runCatching { Instant.parse(it).toEpochMilli() }.getOrNull() }
                    ?: System.currentTimeMillis()

    private fun parseIsoOrNull(value: String?): Long? =
            value?.let { runCatching { Instant.parse(it).toEpochMilli() }.getOrNull() }

    private inline fun <reified T : Enum<T>> enumOrDefault(value: String?, default: T): T =
            value?.let { runCatching { enumValueOf<T>(it) }.getOrElse { default } } ?: default
}
