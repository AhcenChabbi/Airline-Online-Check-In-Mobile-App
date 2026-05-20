package com.airline.checkin.domain.model

data class Passenger(
        val id: String,
        val bookingId: String = "",
        val firstName: String = "",
        val lastName: String = "",
        val passengerType: String = "ADULT",
        val dateOfBirth: String? = null,
        val nationality: String = "",
        val passportNumber: String? = null,
        val passportExpiry: String? = null,
        val passportMrz: String? = null,
        val passportScanUrl: String? = null,
        val isPrimary: Boolean = false,
        val createdAt: String? = null,
        val checkinStatus: String? = null,
        val currentStep: String? = null,
        val hasBoardingPass: Boolean = false
) {
    val name: String
        get() = listOf(firstName, lastName).filter { it.isNotBlank() }.joinToString(" ")

    constructor(id: String, name: String) : this(id = id, firstName = name)
}
