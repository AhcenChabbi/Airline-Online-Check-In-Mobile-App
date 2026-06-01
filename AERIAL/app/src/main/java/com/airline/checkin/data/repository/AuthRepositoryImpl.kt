package com.airline.checkin.data.repository

import com.airline.checkin.data.remote.api.AuthApi
import com.airline.checkin.data.remote.dto.FcmTokenRequestDto
import com.airline.checkin.core.security.TokenManager
import com.airline.checkin.data.remote.api.AuthApi
import com.airline.checkin.data.remote.dto.GoogleAuthRequestDto
import com.airline.checkin.data.remote.dto.LoginRequestDto
import com.airline.checkin.data.remote.dto.RegisterRequestDto
import com.airline.checkin.data.remote.mapper.UserMapper.toDomain
import com.airline.checkin.domain.model.User
import com.airline.checkin.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi
) @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<User> = try {
        val response = authApi.login(LoginRequestDto(email = email, password = password))
        TokenManager.saveTokens(response.accessToken, response.refreshToken)
        Result.success(response.user.toDomain())
    } catch (exception: Exception) {
        Result.failure(exception)
    }

    override suspend fun register(
        name: String,
        phone: String?,
        email: String,
        password: String
    ): Result<User> = try {
        val response = authApi.register(
            RegisterRequestDto(
                fullName = name,
                email = email,
                phone = phone?.trim()?.ifBlank { null },
                password = password
            )
        )
        TokenManager.saveTokens(response.accessToken, response.refreshToken)
        Result.success(response.user.toDomain())
    } catch (exception: Exception) {
        Result.failure(exception)
    }

    override suspend fun getMe(): Result<User> = try {
        val response = authApi.getMe()
        Result.success(response.user.toDomain())
    } catch (exception: Exception) {
        Result.failure(exception)
    }

    override suspend fun googleSignIn(idToken: String): Result<User> = try {
        val response = authApi.googleAuth(GoogleAuthRequestDto(idToken = idToken))
        TokenManager.saveTokens(response.accessToken, response.refreshToken)
        Result.success(response.user.toDomain())
    } catch (exception: Exception) {
        Result.failure(exception)
    }

    override suspend fun logout() {
        tokenManager.clearTokens()
    }

    override suspend fun getCurrentUser(): User? = null

    override suspend fun isLoggedIn(): Boolean = false

    override suspend fun registerFcmToken(fcmToken: String): Result<Unit> = runCatching {
        authApi.registerFcmToken(FcmTokenRequestDto(fcmToken))
    }
}

private fun TokenManager.clearTokens() {
    clear()
}

