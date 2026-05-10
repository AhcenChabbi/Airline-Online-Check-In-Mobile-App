package com.airline.checkin.data.remote.dto

data class BoardingPassDto(
    val id: String,
    val checkinId: String,
    val passengerId: String,
    val seatId: String,
    val qrCodeData: String,
    val pdfUrl: String? = null,
    val isSynced: Boolean = false,
    val issuedAt: String,
    val expiresAt: String,
    val offlinePayload: String? = null
)
