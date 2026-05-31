package com.airline.checkin.domain.usecase.auth

import android.util.Patterns
import com.airline.checkin.domain.model.User
import com.airline.checkin.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        fullName: String,
        email: String,
        phone: String?,
        password: String
    ): Result<User> {
        val trimmedName = fullName.trim()
        if (trimmedName.isBlank()) {
            return Result.failure(IllegalArgumentException("Full name is required"))
        }
        if (trimmedName.length < 2) {
            return Result.failure(IllegalArgumentException("Full name must be at least 2 characters"))
        }

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
        if (trimmedPassword.length < 8) {
            return Result.failure(IllegalArgumentException("Password must be at least 8 characters"))
        }
        if (!trimmedPassword.any { it.isUpperCase() }) {
            return Result.failure(
                IllegalArgumentException("Password must contain at least one uppercase letter")
            )
        }

        return authRepository.register(trimmedName, phone, trimmedEmail, trimmedPassword)
    }
}
