package com.airline.checkin.domain.usecase.auth

import android.util.Patterns
import com.airline.checkin.domain.model.User
import com.airline.checkin.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank()) {
            return Result.failure(IllegalArgumentException("Email is required"))
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            return Result.failure(IllegalArgumentException("Email is invalid"))
        }

        val trimmedPassword = password.trim()
        if (trimmedPassword.isBlank()) {
            return Result.failure(IllegalArgumentException("Password is required"))
        }

        return authRepository.login(trimmedEmail, trimmedPassword)
    }
}
