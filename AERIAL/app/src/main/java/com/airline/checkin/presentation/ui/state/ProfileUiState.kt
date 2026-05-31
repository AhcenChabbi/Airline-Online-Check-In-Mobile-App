package com.airline.checkin.presentation.ui.state

import com.airline.checkin.domain.model.User

sealed class ProfileUiState {
    object Idle : ProfileUiState()

    object Loading : ProfileUiState()

    data class Success(val user: User) : ProfileUiState()

    data class Error(val message: String) : ProfileUiState()
}

