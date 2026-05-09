package com.airline.checkin.presentation.ui.state

data class CheckInUiState(
        val flight: Any? = null,
        val passenger: Any? = null,
        val currentStep: Int = 0,
        val isLoading: Boolean = false,
        val error: String? = null,
        val isOffline: Boolean = false
)
