package com.airline.checkin.domain.repository

import com.airline.checkin.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(name: String, phone: String, email: String, password: String): Result<User>
    suspend fun logout()
    suspend fun getCurrentUser(): User?
    suspend fun isLoggedIn(): Boolean
    suspend fun registerFcmToken(fcmToken: String): Result<Unit>
}
