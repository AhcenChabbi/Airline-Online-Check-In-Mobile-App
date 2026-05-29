package com.airline.checkin.presentation.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airline.checkin.core.services.CheckInSessionStore
import com.airline.checkin.domain.model.Baggage
import com.airline.checkin.domain.model.PassportScanData
import com.airline.checkin.domain.model.SpecialRequest
import com.airline.checkin.domain.usecase.boarding.GenerateBoardingPassUseCase
import com.airline.checkin.domain.usecase.checkin.ConfirmDetailsUseCase
import com.airline.checkin.domain.usecase.checkin.DeclareBaggageUseCase
import com.airline.checkin.domain.usecase.checkin.GetSeatMapUseCase
import com.airline.checkin.domain.usecase.checkin.ScanPassportUseCase
import com.airline.checkin.domain.usecase.checkin.SelectSeatUseCase
import com.airline.checkin.domain.usecase.checkin.StartCheckInUseCase
import com.airline.checkin.domain.usecase.checkin.SubmitSpecialRequestUseCase
import com.airline.checkin.presentation.ui.state.CheckInUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CheckInViewModel @Inject constructor(
    private val startCheckIn: StartCheckInUseCase,
    private val scanPassport: ScanPassportUseCase,
    private val confirmDetails: ConfirmDetailsUseCase,
    private val getSeatMap: GetSeatMapUseCase,
    private val selectSeatUseCase: SelectSeatUseCase,
    private val declareBaggageUseCase: DeclareBaggageUseCase,
    private val submitSpecialRequestUseCase: SubmitSpecialRequestUseCase,
    private val generateBoardingPass: GenerateBoardingPassUseCase,
    private val sessionStore: CheckInSessionStore
) : ViewModel() {
    private val _uiState = MutableStateFlow(CheckInUiState())
    val uiState: StateFlow<CheckInUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            sessionStore.session.collect { session ->
                _uiState.update {
                    it.copy(
                        bookingReference = session.booking?.bookingReference.orEmpty(),
                        flight = session.flight,
                        passenger = session.passenger,
                        checkIn = session.checkIn,
                        seatMap = session.seatMap,
                        selectedSeat = session.selectedSeat,
                        baggage = session.baggage,
                        specialRequests = session.specialRequests,
                        boardingPass = session.boardingPass
                    )
                }
            }
        }
    }

    fun initiateCheckIn(bookingId: String, passengerId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            startCheckIn(bookingId, passengerId)
                .onSuccess { checkIn ->
                    sessionStore.updateCheckIn(checkIn)
                    _uiState.update { it.copy(isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    fun submitPassportAndConfirm(checkinId: String, data: PassportScanData) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            scanPassport(checkinId, data)
                .onSuccess {
                    confirmDetails(checkinId)
                        .onSuccess { (passenger, flight) ->
                            sessionStore.updatePassenger(passenger)
                            sessionStore.updateFlight(flight)
                            _uiState.update { it.copy(isLoading = false) }
                        }
                        .onFailure { error ->
                            _uiState.update { it.copy(isLoading = false, error = error.message) }
                        }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    fun loadSeatMap(checkinId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getSeatMap(checkinId)
                .onSuccess { seats ->
                    sessionStore.updateSeatMap(seats)
                    _uiState.update { it.copy(isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    fun selectSeat(checkinId: String, seatId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            selectSeatUseCase(checkinId, seatId)
                .onSuccess { seat ->
                    sessionStore.updateSelectedSeat(seat)
                    _uiState.update { it.copy(isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    fun declareBaggage(checkinId: String, bags: List<Baggage>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            declareBaggageUseCase(checkinId, bags)
                .onSuccess {
                    sessionStore.updateBaggage(bags)
                    _uiState.update { it.copy(isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    fun submitSpecialRequests(checkinId: String, requests: List<SpecialRequest>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            submitSpecialRequestUseCase(checkinId, requests)
                .onSuccess {
                    sessionStore.updateSpecialRequests(requests)
                    _uiState.update { it.copy(isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    fun confirmCheckIn(checkinId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            generateBoardingPass(checkinId)
                .onSuccess { boardingPass ->
                    sessionStore.updateBoardingPass(boardingPass)
                    _uiState.update { it.copy(isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }
}
