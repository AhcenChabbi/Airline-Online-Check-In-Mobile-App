package com.airline.checkin.data.remote.mapper

import com.airline.checkin.data.remote.dto.BookingLookupResponseDto
import com.airline.checkin.data.remote.dto.FlightSummaryDto
import com.airline.checkin.data.remote.dto.PassengerSummaryDto
import com.airline.checkin.domain.model.BookingLookup
import com.airline.checkin.domain.model.Flight
import com.airline.checkin.domain.model.Passenger

object BookingMapper {
        fun toDomain(dto: BookingLookupResponseDto): BookingLookup =
                BookingLookup(
                        bookingId = dto.bookingId,
                        bookingReference = dto.bookingReference,
                        status = dto.status,
                        isCheckinOpen = dto.isCheckinOpen,
                        flight = dto.flight.toDomain(),
                        passengers = dto.passengers.map { it.toDomain(dto.bookingId) }
                )

        private fun FlightSummaryDto.toDomain(): Flight =
                Flight(
                        id = flightNumber,
                        flightNumber = flightNumber,
                        airlineCode = airlineCode,
                        airlineName = "AERIAL",
                        originIata = originIata,
                        originCity = originIata,
                        destinationIata = destIata,
                        destinationCity = destIata,
                        departureTime = departureAt,
                        arrivalTime = arrivalAt,
                        date = departureAt,
                        aircraftType = aircraftType,
                        totalRows = 30,
                        seatsPerRow = 6,
                        status = status
                )

        private fun PassengerSummaryDto.toDomain(bookingId: String): Passenger =
                Passenger(
                        id = id,
                        bookingId = bookingId,
                        firstName = firstName,
                        lastName = lastName,
                        passengerType = passengerType,
                        isPrimary = isPrimary,
                        checkinStatus = checkinStatus,
                        currentStep = currentStep,
                        hasBoardingPass = hasBoardingPass
                )
}
