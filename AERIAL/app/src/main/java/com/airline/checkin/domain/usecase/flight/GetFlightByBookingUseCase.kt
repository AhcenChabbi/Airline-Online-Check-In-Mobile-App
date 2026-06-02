package com.airline.checkin.domain.usecase.flight

import com.airline.checkin.domain.model.BookingLookup
import com.airline.checkin.domain.repository.FlightRepository
import javax.inject.Inject

class GetFlightByBookingUseCase @Inject constructor(
    private val flightRepository: FlightRepository
) {
    suspend operator fun invoke(
        bookingReference: String,
        lastName: String
    ): Result<BookingLookup> = flightRepository.lookupBooking(bookingReference, lastName)
}
