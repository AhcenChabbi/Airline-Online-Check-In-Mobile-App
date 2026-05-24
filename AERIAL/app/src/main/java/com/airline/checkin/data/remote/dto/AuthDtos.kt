package com.airline.checkin.data.remote.dto

data class RegisterRequestDto(
        val fullName: String,
        val email: String,
        val phone: String? = null,
        val password: String
)

data class LoginRequestDto(val email: String, val password: String)

data class GoogleAuthRequestDto(val idToken: String)

data class RefreshTokenRequestDto(val refreshToken: String)

data class AuthResponseDto(val accessToken: String, val refreshToken: String, val user: UserDto)

data class UserMeResponseDto(val user: UserDto)

data class FcmTokenRequestDto(val fcmToken: String)

data class MessageResponseDto(val message: String)

data class HealthResponseDto(val message: String)
