package com.airline.checkin.core.services

import com.airline.checkin.domain.model.Baggage
import com.airline.checkin.domain.model.BoardingPass
import com.airline.checkin.domain.model.BookingLookup
import com.airline.checkin.domain.model.CheckIn
import com.airline.checkin.domain.model.Flight
import com.airline.checkin.domain.model.Passenger
import com.airline.checkin.domain.model.Seat
import com.airline.checkin.domain.model.SpecialRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CheckInSessionStore @Inject constructor() {
    private val _session = MutableStateFlow(CheckInSession())
    val session: StateFlow<CheckInSession> = _session

    fun reset() {
        _session.value = CheckInSession()
    }

    fun updateBooking(booking: BookingLookup) {
        val passenger = booking.passengers.firstOrNull { p -> p.isPrimary }
            ?: booking.passengers.firstOrNull()
        _session.update { it.copy(booking = booking, flight = booking.flight, passenger = passenger) }
    }

    fun updateCheckIn(checkIn: CheckIn) {
        _session.update { it.copy(checkIn = checkIn) }
    }

    fun updatePassenger(passenger: Passenger) {
        _session.update { it.copy(passenger = passenger) }
    }

    fun updateFlight(flight: Flight) {
        _session.update { it.copy(flight = flight) }
    }

    fun updateSeatMap(seats: List<Seat>) {
        _session.update { it.copy(seatMap = seats) }
    }

    fun updateSelectedSeat(seat: Seat) {
        _session.update { it.copy(selectedSeat = seat) }
    }

    fun updateBaggage(baggage: List<Baggage>) {
        _session.update { it.copy(baggage = baggage) }
    }

    fun updateSpecialRequests(requests: List<SpecialRequest>) {
        _session.update { it.copy(specialRequests = requests) }
    }

    fun updateBoardingPass(boardingPass: BoardingPass) {
        _session.update { it.copy(boardingPass = boardingPass) }
    }
}

data class CheckInSession(
    val booking: BookingLookup? = null,
    val checkIn: CheckIn? = null,
    val flight: Flight? = null,
    val passenger: Passenger? = null,
    val seatMap: List<Seat> = emptyList(),
    val selectedSeat: Seat? = null,
    val baggage: List<Baggage> = emptyList(),
    val specialRequests: List<SpecialRequest> = emptyList(),
    val boardingPass: BoardingPass? = null
)
