package com.airline.checkin.presentation.ui.state

data class FlightUiState(
        val bookingRef: String = "",
        val passenger: String = "",
        val flights: List<Any> = emptyList(),
        val selectedFlight: Any? = null,
        val isLoading: Boolean = false,
        val error: String? = null,
        val isOffline: Boolean = false
)
