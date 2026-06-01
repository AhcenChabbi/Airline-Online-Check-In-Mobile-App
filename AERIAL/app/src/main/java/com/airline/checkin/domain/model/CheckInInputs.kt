package com.airline.checkin.domain.model

data class PassportScanData(
    val passportNumber: String,
    val passportExpiry: String,
    val passportMrz: String? = null,
    val passportScanUrl: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val nationality: String? = null,
    val dateOfBirth: String? = null
)
