package com.airline.checkin.data.repository

import com.airline.checkin.data.remote.api.FlightApi
import com.airline.checkin.data.remote.dto.BookingLookupRequestDto
import com.airline.checkin.data.remote.mapper.BookingMapper
import com.airline.checkin.domain.model.BookingLookup
import com.airline.checkin.domain.repository.FlightRepository
import javax.inject.Inject

class FlightRepositoryImpl @Inject constructor(
    private val flightApi: FlightApi
) : FlightRepository {
    override suspend fun lookupBooking(
        bookingReference: String,
        lastName: String
    ): Result<BookingLookup> = runCatching {
        val response = flightApi.lookupBooking(
            BookingLookupRequestDto(
                bookingReference = bookingReference,
                lastName = lastName
            )
        )
        BookingMapper.toDomain(response)
    }
}
