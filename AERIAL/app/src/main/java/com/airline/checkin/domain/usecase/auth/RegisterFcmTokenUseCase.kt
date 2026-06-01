package com.airline.checkin.domain.usecase.auth

import com.airline.checkin.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterFcmTokenUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(token: String): Result<Unit> =
        authRepository.registerFcmToken(token)
}
