package com.airline.checkin.presentation.ui.state

data class AuthUiState(
        val email: String = "",
        val password: String = "",
        val isLoading: Boolean = false,
        val error: String? = null,
        val isAuthenticated: Boolean = false
)
