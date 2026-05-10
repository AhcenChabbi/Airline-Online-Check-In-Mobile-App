package com.airline.checkin.data.remote.dto

data class FlightDto(
    val id: String,
    val flightNumber: String,
    val airlineCode: String,
    val originIata: String,
    val destinationIata: String,
    val departureAt: String,
    val arrivalAt: String,
    val status: String
)
