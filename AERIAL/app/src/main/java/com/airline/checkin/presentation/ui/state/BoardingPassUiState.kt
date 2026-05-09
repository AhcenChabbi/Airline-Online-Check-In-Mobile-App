package com.airline.checkin.presentation.ui.state

data class BoardingPassUiState(
        val boardingPass: Any? = null,
        val qrPayload: String = "",
        val isLoading: Boolean = false,
        val error: String? = null,
        val isOffline: Boolean = false
)
