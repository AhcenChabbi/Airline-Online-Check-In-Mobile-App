package com.airline.checkin.data.remote.dto

data class CheckInDto(
        val id: String,
        val bookingId: String,
        val passengerId: String,
        val status: String = "INITIATED",
        val currentStep: String = "PASSPORT_SCAN",
        val startedAt: String? = null,
        val completedAt: String? = null,
        val ipAddress: String? = null
)
