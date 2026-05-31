package com.airline.checkin.presentation.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.airline.checkin.core.security.TokenManager
import com.airline.checkin.core.security.isLoggedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {
    val isLoggedIn: Boolean = tokenManager.isLoggedIn()
}

