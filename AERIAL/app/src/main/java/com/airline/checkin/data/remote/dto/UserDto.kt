package com.airline.checkin.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UserDto(
        @SerializedName("id") val id: String,
        @SerializedName("fullName") val fullName: String,
        @SerializedName("email") val email: String,
        @SerializedName("phone") val phone: String? = null,
        @SerializedName("avatarUrl") val avatarUrl: String? = null,
        @SerializedName("googleId") val googleId: String? = null,
        @SerializedName("createdAt") val createdAt: String? = null,
        @SerializedName("updatedAt") val updatedAt: String? = null
)
