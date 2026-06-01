package com.airline.checkin.presentation.ui.screens.boarding

import com.airline.checkin.domain.model.BoardingPass
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

internal data class BoardingPassCardData(
        val passengerName: String,
        val flightNumber: String,
        val from: String,
        val fromCity: String,
        val to: String,
        val toCity: String,
        val date: String,
        val gate: String,
        val seat: String,
        val boardingTime: String,
        val bookingRef: String,
        val qrCodeData: String
)

internal fun toCardData(boardingPass: BoardingPass): BoardingPassCardData {
    val payload = boardingPass.offlinePayload
    val passengerName =
            listOfNotNull(payload?.passenger?.firstName, payload?.passenger?.lastName)
                    .joinToString(" ")
                    .ifBlank { "Passenger" }
    val flightNumber = payload?.flight?.flightNumber ?: "-"
    val from = payload?.flight?.origin ?: "-"
    val to = payload?.flight?.destination ?: "-"
    val date = formatDate(payload?.flight?.departureAt) ?: "-"
    val boardingTime = formatTime(payload?.flight?.departureAt) ?: "-"
    val seat = payload?.seat?.seatCode ?: "-"
    val bookingRef = boardingPass.checkinId.take(6).uppercase()

    return BoardingPassCardData(
            passengerName = passengerName,
            flightNumber = flightNumber,
            from = from,
            fromCity = from,
            to = to,
            toCity = to,
            date = date,
            gate = "TBD",
            seat = seat,
            boardingTime = boardingTime,
            bookingRef = bookingRef,
            qrCodeData = boardingPass.qrCodeData
    )
}

private fun formatDate(iso: String?): String? {
    val instant = iso?.let { runCatching { Instant.parse(it) }.getOrNull() } ?: return null
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
    return formatter.format(instant.atZone(ZoneId.systemDefault()).toLocalDate())
}

private fun formatTime(iso: String?): String? {
    val instant = iso?.let { runCatching { Instant.parse(it) }.getOrNull() } ?: return null
    val formatter = DateTimeFormatter.ofPattern("hh:mm a")
    return formatter.format(instant.atZone(ZoneId.systemDefault()).toLocalTime())
}
