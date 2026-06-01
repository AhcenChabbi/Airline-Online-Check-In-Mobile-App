package com.airline.checkin.data.repository

import com.airline.checkin.data.remote.api.AuthApi
import com.airline.checkin.data.remote.dto.FcmTokenRequestDto
import com.airline.checkin.domain.model.User
import com.airline.checkin.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi
) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<User> =
        Result.failure(UnsupportedOperationException("Not implemented"))

    override suspend fun register(
        name: String,
        phone: String,
        email: String,
        password: String
    ): Result<User> = Result.failure(UnsupportedOperationException("Not implemented"))

    override suspend fun logout() {
        // TODO: Implement logout
    }

    override suspend fun getCurrentUser(): User? = null

    override suspend fun isLoggedIn(): Boolean = false

    override suspend fun registerFcmToken(fcmToken: String): Result<Unit> = runCatching {
        authApi.registerFcmToken(FcmTokenRequestDto(fcmToken))
    }
}
