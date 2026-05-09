package com.airline.checkin.data.remote.mapper

import com.airline.checkin.data.remote.dto.FlightDto
import com.airline.checkin.domain.model.Flight

object FlightMapper {
    fun toDomain(dto: FlightDto): Flight =
        Flight(
            id = dto.id,
            number = dto.number,
            airline = "",
            origin = "",
            originCode = "",
            destination = "",
            destinationCode = "",
            departureTime = "",
            arrivalTime = "",
            date = ""
        )
}
