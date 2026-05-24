package com.airline.checkin.data.remote.dto

data class UserDto(
        val id: String,
        val fullName: String,
        val email: String,
        val phone: String? = null,
        val avatarUrl: String? = null,
        val googleId: String? = null,
        val createdAt: String? = null,
        val updatedAt: String? = null
)
