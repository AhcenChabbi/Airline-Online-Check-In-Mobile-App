package com.airline.checkin.presentation.ui.state

import com.airline.checkin.domain.model.BookingLookup

data class FlightUiState(
        val bookingRef: String = "",
        val lastName: String = "",
        val bookingLookup: BookingLookup? = null,
        val isLoading: Boolean = false,
        val error: String? = null,
        val isOffline: Boolean = false
)
