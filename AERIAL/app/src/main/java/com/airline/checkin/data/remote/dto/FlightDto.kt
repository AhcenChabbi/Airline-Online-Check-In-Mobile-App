package com.airline.checkin.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FlightDto(
        val id: String? = null,
        val flightNumber: String,
        val airlineCode: String,
        val originIata: String,
        @SerializedName("destIata") val destIata: String,
        val departureAt: String,
        val arrivalAt: String,
        val aircraftType: String? = null,
        val totalRows: Int? = null,
        val seatsPerRow: Int? = null,
        val status: String,
        val createdAt: String? = null
)
