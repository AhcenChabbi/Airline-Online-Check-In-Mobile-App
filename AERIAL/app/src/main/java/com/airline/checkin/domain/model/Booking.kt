package com.airline.checkin.domain.model

data class Booking(
    val id: String,
    val userId: String? = null,
    val flightId: String,
    val bookingReference: String,
    val lastName: String,
    val status: String = "PENDING",
    val bookedAt: String,
    val expiresAt: String? = null
)
