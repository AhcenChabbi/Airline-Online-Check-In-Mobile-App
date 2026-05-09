package com.airline.checkin.domain.model

data class Flight(
    val id: String,
    val number: String,
    val airline: String,
    val origin: String,
    val originCode: String,
    val destination: String,
    val destinationCode: String,
    val departureTime: String,
    val arrivalTime: String,
    val date: String,
    val gate: String = "",
    val status: String = "On Time"
)
