package com.airline.checkin.presentation.ui.state

data class SeatUiState(
        val seatMap: List<Any> = emptyList(),
        val selectedSeat: Any? = null,
        val isLoading: Boolean = false,
        val error: String? = null
)
