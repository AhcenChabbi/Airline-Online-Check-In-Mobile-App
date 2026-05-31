package com.airline.checkin.presentation.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airline.checkin.domain.usecase.auth.GetMeUseCase
import com.airline.checkin.domain.usecase.auth.LoginUseCase
import com.airline.checkin.domain.usecase.auth.LogoutUseCase
import com.airline.checkin.domain.usecase.auth.RegisterUseCase
import com.airline.checkin.domain.usecase.auth.SignInWithGoogleUseCase
import com.airline.checkin.presentation.ui.state.AuthUiState
import com.airline.checkin.presentation.ui.state.ProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val getMeUseCase: GetMeUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {
    private val mutableUiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = mutableUiState.asStateFlow()

    private val mutableProfileState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val profileState: StateFlow<ProfileUiState> = mutableProfileState.asStateFlow()

    private val mutableLogoutState = MutableStateFlow(false)
    val logoutState: StateFlow<Boolean> = mutableLogoutState.asStateFlow()

    fun register(fullName: String, email: String, phone: String?, password: String) {
        mutableUiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val result = registerUseCase(fullName, email, phone, password)
            mutableUiState.value = result
                .fold(
                    onSuccess = { user -> AuthUiState.Success(user) },
                    onFailure = { throwable ->
                        AuthUiState.Error(throwable.message ?: "An error occurred")
                    }
                )
        }
    }

    fun login(email: String, password: String) {
        mutableUiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val result = loginUseCase(email, password)
            mutableUiState.value = result
                .fold(
                    onSuccess = { user -> AuthUiState.Success(user) },
                    onFailure = { throwable ->
                        AuthUiState.Error(throwable.message ?: "An error occurred")
                    }
                )
        }
    }

    fun signInWithGoogle(idToken: String) {
        mutableUiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val result = signInWithGoogleUseCase(idToken)
            mutableUiState.value = result
                .fold(
                    onSuccess = { user -> AuthUiState.Success(user) },
                    onFailure = { throwable ->
                        AuthUiState.Error(throwable.message ?: "An error occurred")
                    }
                )
        }
    }

    fun getMe() {
        mutableProfileState.value = ProfileUiState.Loading
        viewModelScope.launch {
            val result = getMeUseCase()
            mutableProfileState.value = result
                .fold(
                    onSuccess = { user -> ProfileUiState.Success(user) },
                    onFailure = { throwable ->
                        ProfileUiState.Error(throwable.message ?: "An error occurred")
                    }
                )
        }
    }

    fun resetProfileState() {
        mutableProfileState.value = ProfileUiState.Idle
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            mutableLogoutState.value = true
        }
    }

    fun resetLogoutState() {
        mutableLogoutState.value = false
    }

    fun resetState() {
        mutableUiState.value = AuthUiState.Idle
    }
}
