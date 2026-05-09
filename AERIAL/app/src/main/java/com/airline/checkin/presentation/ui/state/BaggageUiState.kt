package com.airline.checkin.presentation.ui.state

data class BaggageUiState(
        val baggage: List<Any> = emptyList(),
        val totalWeight: Double = 0.0,
        val maxWeight: Double = 23.0,
        val isLoading: Boolean = false,
        val error: String? = null
)
