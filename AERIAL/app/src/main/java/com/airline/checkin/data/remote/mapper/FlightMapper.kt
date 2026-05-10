package com.airline.checkin.data.remote.mapper

import com.airline.checkin.data.remote.dto.FlightDto
import com.airline.checkin.domain.model.Flight

object FlightMapper {
    fun toDomain(dto: FlightDto): Flight =
        Flight(
            id = dto.id,
            flightNumber = dto.flightNumber,
            airlineCode = dto.airlineCode,
            airlineName = "AERIAL", // Default or derived
            originIata = dto.originIata,
            originCity = "Origin City", // Would be mapped from IATA in a real app
            destinationIata = dto.destinationIata,
            destinationCity = "Dest City",
            departureTime = dto.departureAt,
            arrivalTime = dto.arrivalAt,
            date = "Oct 24, 2023", // Derived from departureAt
            status = dto.status
        )
}
