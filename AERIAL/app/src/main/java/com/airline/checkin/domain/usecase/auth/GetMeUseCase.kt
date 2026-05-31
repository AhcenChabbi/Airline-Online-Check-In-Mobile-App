package com.airline.checkin.domain.usecase.auth

import com.airline.checkin.domain.model.User
import com.airline.checkin.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetMeUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<User> = authRepository.getMe()
}

