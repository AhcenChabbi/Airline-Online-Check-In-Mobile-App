package com.airline.checkin.presentation.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airline.checkin.core.services.CheckInSessionStore
import com.airline.checkin.domain.usecase.flight.GetFlightByBookingUseCase
import com.airline.checkin.presentation.ui.state.FlightUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class FlightViewModel @Inject constructor(
    private val getFlightByBooking: GetFlightByBookingUseCase,
    private val sessionStore: CheckInSessionStore
) : ViewModel() {
    private val _uiState = MutableStateFlow(FlightUiState())
    val uiState: StateFlow<FlightUiState> = _uiState.asStateFlow()

    fun lookupBooking(bookingReference: String, lastName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = getFlightByBooking(bookingReference, lastName)
            result
                .onSuccess { bookingLookup ->
                    sessionStore.updateBooking(bookingLookup)
                    _uiState.update {
                        it.copy(
                            bookingRef = bookingReference,
                            lastName = lastName,
                            bookingLookup = bookingLookup,
                            isLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }
}
