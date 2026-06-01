package com.airline.checkin.domain.model

data class Baggage(
        val id: String = "",
        val checkinId: String = "",
        val bagType: String = "CARRY_ON",
        val quantity: Int = 1,
        val weightKg: Double? = null,
        val status: String = "DECLARED",
        val tagNumber: String? = null,
        val createdAt: String? = null
)
