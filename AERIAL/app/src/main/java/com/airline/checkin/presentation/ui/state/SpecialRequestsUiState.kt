package com.airline.checkin.presentation.ui.state

data class SpecialRequestsUiState(
        val wheelchairAssistance: Boolean = false,
        val mealPreference: String = "",
        val seatPreference: String = "",
        val isLoading: Boolean = false,
        val error: String? = null
)
