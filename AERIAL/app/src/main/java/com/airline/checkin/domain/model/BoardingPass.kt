package com.airline.checkin.domain.model

data class BoardingPass(
        val id: String,
        val checkinId: String,
        val passengerId: String,
        val seatId: String,
        val qrCodeData: String,
        val qrCodeUrl: String? = null,
        val pdfUrl: String? = null,
        val isSynced: Boolean = false,
        val syncedAt: String? = null,
        val issuedAt: String,
        val expiresAt: String,
        val offlinePayload: BoardingPassOfflinePayload? = null
)

data class BoardingPassOfflinePayload(
        val passenger: BoardingPassPassengerPayload,
        val flight: BoardingPassFlightPayload,
        val seat: BoardingPassSeatPayload
)

data class BoardingPassPassengerPayload(
        val firstName: String? = null,
        val lastName: String? = null,
        val passportNumber: String? = null
)

data class BoardingPassFlightPayload(
        val flightNumber: String,
        val origin: String,
        val destination: String,
        val departureAt: String,
        val arrivalAt: String
)

data class BoardingPassSeatPayload(val seatCode: String, val seatClass: String)
