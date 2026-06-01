package com.airline.checkin.presentation.ui.state

import com.airline.checkin.domain.model.Baggage
import com.airline.checkin.domain.model.BoardingPass
import com.airline.checkin.domain.model.CheckIn
import com.airline.checkin.domain.model.Flight
import com.airline.checkin.domain.model.Passenger
import com.airline.checkin.domain.model.Seat
import com.airline.checkin.domain.model.SpecialRequest

data class CheckInUiState(
        val bookingReference: String = "",
        val flight: Flight? = null,
        val passenger: Passenger? = null,
        val checkIn: CheckIn? = null,
        val seatMap: List<Seat> = emptyList(),
        val selectedSeat: Seat? = null,
        val baggage: List<Baggage> = emptyList(),
        val specialRequests: List<SpecialRequest> = emptyList(),
        val boardingPass: BoardingPass? = null,
        val currentStep: Int = 0,
        val isLoading: Boolean = false,
        val error: String? = null,
        val isOffline: Boolean = false
)
