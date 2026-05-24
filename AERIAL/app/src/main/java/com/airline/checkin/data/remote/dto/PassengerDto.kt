package com.airline.checkin.data.remote.dto

data class PassengerDto(
        val id: String,
        val bookingId: String,
        val firstName: String,
        val lastName: String,
        val passengerType: String,
        val dateOfBirth: String,
        val nationality: String,
        val passportNumber: String? = null,
        val passportExpiry: String? = null,
        val passportMrz: String? = null,
        val passportScanUrl: String? = null,
        val isPrimary: Boolean = false,
        val createdAt: String
)
