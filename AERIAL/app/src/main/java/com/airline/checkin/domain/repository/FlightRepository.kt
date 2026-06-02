package com.airline.checkin.domain.repository

import com.airline.checkin.domain.model.BookingLookup

interface FlightRepository {
    suspend fun lookupBooking(bookingReference: String, lastName: String): Result<BookingLookup>
}
