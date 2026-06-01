package com.airline.checkin.presentation.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airline.checkin.core.services.CheckInSessionStore
import com.airline.checkin.domain.usecase.boarding.DownloadBoardingPassPdfUseCase
import com.airline.checkin.domain.usecase.boarding.GetBoardingPassUseCase
import com.airline.checkin.presentation.ui.state.BoardingPassUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class BoardingPassViewModel @Inject constructor(
    private val getBoardingPass: GetBoardingPassUseCase,
    private val downloadBoardingPassPdf: DownloadBoardingPassPdfUseCase,
    private val sessionStore: CheckInSessionStore
) : ViewModel() {
    private val _uiState = MutableStateFlow(BoardingPassUiState())
    val uiState: StateFlow<BoardingPassUiState> = _uiState.asStateFlow()

    fun loadBoardingPass(checkinId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getBoardingPass(checkinId)
                .onSuccess { boardingPass ->
                    sessionStore.updateBoardingPass(boardingPass)
                    _uiState.update { it.copy(isLoading = false, boardingPass = boardingPass) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
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
}
