package com.airline.checkin.domain.model

data class Flight(
        val id: String,
        val flightNumber: String,
        val airlineCode: String,
        val airlineName: String = "AERIAL",
        val originIata: String,
        val originCity: String,
        val destinationIata: String,
        val destinationCity: String,
        val departureTime: String,
        val arrivalTime: String,
        val date: String,
        val aircraftType: String = "A320",
        val totalRows: Int = 30,
        val seatsPerRow: Int = 6,
        val gate: String = "",
        val status: String = "SCHEDULED",
        val createdAt: String? = null
) {
    val destIata: String
        get() = destinationIata

    val departureAt: String
        get() = departureTime

    val arrivalAt: String
        get() = arrivalTime
}
