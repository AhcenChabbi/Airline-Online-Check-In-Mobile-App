package com.airline.checkin.presentation.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airline.checkin.core.network.NetworkMonitor
import com.airline.checkin.core.services.CheckInSessionStore
import com.airline.checkin.domain.model.BoardingPass
import com.airline.checkin.domain.usecase.boarding.DownloadBoardingPassPdfUseCase
import com.airline.checkin.domain.usecase.boarding.GetBoardingPassUseCase
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
class BoardingPassViewModel @Inject constructor(
    private val getBoardingPass: GetBoardingPassUseCase,
    private val downloadBoardingPassPdf: DownloadBoardingPassPdfUseCase,
    private val sessionStore: CheckInSessionStore,
    private val getOfflineBoardingPass: GetOfflineBoardingPassUseCase
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
            getBoardingPass(checkinId)
                .onSuccess { boardingPass ->
                    sessionStore.updateBoardingPass(boardingPass)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            boardingPass = boardingPass,
                            qrPayload = boardingPass.qrCodeData,
                            pdfPath = resolvePdfPath(boardingPass)
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
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

    fun downloadPdf(checkinId: String, onDownloaded: (ByteArray) -> Unit) {
        viewModelScope.launch {
            downloadBoardingPassPdf(checkinId)
                .onSuccess { bytes -> onDownloaded(bytes) }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message) }
                }
        }
    }

    private fun observeCachedBoardingPass(checkinId: String) {
        cachedJob?.cancel()
        cachedJob = viewModelScope.launch {
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