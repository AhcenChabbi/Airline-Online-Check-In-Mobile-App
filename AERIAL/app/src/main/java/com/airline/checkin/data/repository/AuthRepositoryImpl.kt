package com.airline.checkin.data.repository

import com.airline.checkin.domain.model.User
import com.airline.checkin.domain.repository.AuthRepository

class AuthRepositoryImpl : AuthRepository {
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
}
