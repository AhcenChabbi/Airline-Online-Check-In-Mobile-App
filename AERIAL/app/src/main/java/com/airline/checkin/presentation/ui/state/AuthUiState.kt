package com.airline.checkin.presentation.ui.state

import com.airline.checkin.domain.model.User

sealed class AuthUiState {
    object Idle : AuthUiState()

    object Loading : AuthUiState()

    data class Success(val user: User) : AuthUiState()

    data class Error(val message: String) : AuthUiState()
}
