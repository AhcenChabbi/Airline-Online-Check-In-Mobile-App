package com.airline.checkin.presentation.ui.state

import com.airline.checkin.domain.model.BoardingPass

data class BoardingPassUiState(
        val boardingPass: BoardingPass? = null,
        val qrPayload: String = "",
        val pdfPath: String? = null,
        val isLoading: Boolean = false,
        val isDownloading: Boolean = false,
        val error: String? = null,
        val isOffline: Boolean = false
)
