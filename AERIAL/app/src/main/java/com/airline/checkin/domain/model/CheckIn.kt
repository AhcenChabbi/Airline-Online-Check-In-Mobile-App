package com.airline.checkin.domain.model

data class CheckIn(
    val id: String,
    val bookingId: String,
    val passengerId: String,
    val status: String = "INITIATED",
    val currentStep: String = "PASSPORT_SCAN",
    val startedAt: String,
    val completedAt: String? = null,
    val ipAddress: String? = null
)
