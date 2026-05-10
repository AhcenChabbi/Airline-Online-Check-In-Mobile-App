package com.airline.checkin.domain.model

data class User(
    val id: String,
    val fullName: String,
    val email: String,
    val phone: String? = null,
    val googleId: String? = null,
    val avatarUrl: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
