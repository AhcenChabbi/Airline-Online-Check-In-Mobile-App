package com.airline.checkin.presentation.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airline.checkin.core.network.NetworkMonitor
import com.airline.checkin.domain.model.BoardingPass
import com.airline.checkin.domain.usecase.boarding.DownloadBoardingPassPdfUseCase
import com.airline.checkin.domain.usecase.boarding.GenerateBoardingPassUseCase
import com.airline.checkin.domain.usecase.boarding.GetOfflineBoardingPassUseCase
import com.airline.checkin.presentation.ui.state.BoardingPassUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class BoardingPassViewModel
@Inject
constructor(
        private val generateBoardingPass: GenerateBoardingPassUseCase,
        private val getOfflineBoardingPass: GetOfflineBoardingPassUseCase,
        private val downloadBoardingPassPdf: DownloadBoardingPassPdfUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(BoardingPassUiState())
    val uiState: StateFlow<BoardingPassUiState> = _uiState.asStateFlow()

    private var cachedJob: Job? = null

    init {
        viewModelScope.launch {
            NetworkMonitor.isOnline.collectLatest { isOnline ->
                _uiState.update { it.copy(isOffline = !isOnline) }
            }
        }
    }

    fun loadBoardingPass(checkinId: String) {
        if (checkinId.isBlank()) {
            cachedJob?.cancel()
            _uiState.value = BoardingPassUiState(error = "Missing check-in id.")
            return
        }

        observeCachedBoardingPass(checkinId)

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            generateBoardingPass(checkinId)
                    .onSuccess { boardingPass ->
                        _uiState.update {
                            it.copy(
                                    boardingPass = boardingPass,
                                    qrPayload = boardingPass.qrCodeData,
                                    pdfPath = resolvePdfPath(boardingPass),
                                    isLoading = false,
                                    error = null
                            )
                        }
                    }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(isLoading = false, error = error.message ?: "Unable to load boarding pass.")
                        }
                    }
        }
    }

    fun loadOfflineBoardingPass(checkinId: String) {
        if (checkinId.isBlank()) {
            cachedJob?.cancel()
            _uiState.value = BoardingPassUiState(error = "Missing check-in id.")
            return
        }

        observeCachedBoardingPass(checkinId)
    }

    fun downloadPdf(checkinId: String) {
        if (checkinId.isBlank()) {
            _uiState.update { it.copy(error = "Missing check-in id.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isDownloading = true, error = null) }
            downloadBoardingPassPdf(checkinId)
                    .onSuccess { file ->
                        _uiState.update {
                            it.copy(
                                    pdfPath = file.toURI().toString(),
                                    isDownloading = false
                            )
                        }
                    }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(
                                    isDownloading = false,
                                    error = error.message ?: "Unable to download PDF."
                            )
                        }
                    }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun observeCachedBoardingPass(checkinId: String) {
        cachedJob?.cancel()
        cachedJob =
                viewModelScope.launch {
                    getOfflineBoardingPass(checkinId).collectLatest { cached ->
                        if (cached != null) {
                            _uiState.update {
                                it.copy(
                                        boardingPass = cached,
                                        qrPayload = cached.qrCodeData,
                                        pdfPath = resolvePdfPath(cached),
                                        error = null
                                )
                            }
                        }
                    }
                }
    }

    private fun resolvePdfPath(boardingPass: BoardingPass): String? {
        val url = boardingPass.pdfUrl ?: return null
        return if (url.startsWith("file:") || url.startsWith("/")) url else null
    }
}
