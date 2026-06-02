package com.airline.checkin.data.remote.mapper

import com.airline.checkin.data.remote.dto.FlightDto
import com.airline.checkin.domain.model.Flight

object FlightMapper {
    fun toDomain(dto: FlightDto): Flight =
            Flight(
                    id = dto.id ?: "",
                    flightNumber = dto.flightNumber,
                    airlineCode = dto.airlineCode,
                    airlineName = "AERIAL",
                    originIata = dto.originIata,
                    originCity = "Origin City",
                    destinationIata = dto.destIata,
                    destinationCity = "Dest City",
                    departureTime = dto.departureAt,
                    arrivalTime = dto.arrivalAt,
                    date = "June 02, 2026",
                    aircraftType = dto.aircraftType ?: "A320",
                    totalRows = dto.totalRows ?: 30,
                    seatsPerRow = dto.seatsPerRow ?: 6,
                    status = dto.status,
                    createdAt = dto.createdAt
            )
}
