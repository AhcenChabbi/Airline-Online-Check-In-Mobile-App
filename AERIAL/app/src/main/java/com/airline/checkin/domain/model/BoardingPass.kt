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
    val issuedAt: String,
    val expiresAt: String,
    val offlinePayload: String? = null
)
