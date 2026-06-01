package com.airline.checkin.presentation.ui.state

import com.airline.checkin.domain.model.BoardingPass

data class BoardingPassUiState(
        val boardingPass: BoardingPass? = null,
        val qrPayload: String? = null,
        val pdfPath: String? = null,
        val isLoading: Boolean = false,
        val error: String? = null,
        val isOffline: Boolean = false
)
