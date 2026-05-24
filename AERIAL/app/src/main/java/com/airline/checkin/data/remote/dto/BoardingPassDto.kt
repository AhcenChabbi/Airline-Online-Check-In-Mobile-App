package com.airline.checkin.data.remote.dto

import com.google.gson.annotations.SerializedName

data class BoardingPassDto(
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
        val offlinePayload: BoardingPassOfflinePayloadDto? = null
)

data class BoardingPassOfflinePayloadDto(
        val passenger: BoardingPassPassengerDto,
        val flight: BoardingPassFlightDto,
        val seat: BoardingPassSeatDto
)

data class BoardingPassPassengerDto(
        val firstName: String? = null,
        val lastName: String? = null,
        val passportNumber: String? = null
)

data class BoardingPassFlightDto(
        val flightNumber: String,
        val origin: String,
        val destination: String,
        val departureAt: String,
        val arrivalAt: String
)

data class BoardingPassSeatDto(
        val seatCode: String,
        @SerializedName("class") val seatClass: String
)
