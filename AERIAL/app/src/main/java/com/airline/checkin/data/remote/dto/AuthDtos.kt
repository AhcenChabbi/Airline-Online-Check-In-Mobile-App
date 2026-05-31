package com.airline.checkin.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RegisterRequestDto(
        @SerializedName("fullName") val fullName: String,
        @SerializedName("email") val email: String,
        @SerializedName("phone") val phone: String? = null,
        @SerializedName("password") val password: String
)

data class LoginRequestDto(
        @SerializedName("email") val email: String,
        @SerializedName("password") val password: String
)

data class GoogleAuthRequestDto(
        @SerializedName("idToken") val idToken: String
)

data class RefreshTokenRequestDto(
        @SerializedName("refreshToken") val refreshToken: String
)

data class AuthResponseDto(
        @SerializedName("accessToken") val accessToken: String,
        @SerializedName("refreshToken") val refreshToken: String,
        @SerializedName("user") val user: UserDto
)

data class UserMeResponseDto(
        @SerializedName("user") val user: UserDto
)

data class FcmTokenRequestDto(
        @SerializedName("fcmToken") val fcmToken: String
)

data class MessageResponseDto(
        @SerializedName("message") val message: String
)

data class HealthResponseDto(
        @SerializedName("message") val message: String
)
