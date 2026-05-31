package com.airline.checkin.data.remote.mapper

import com.airline.checkin.data.remote.dto.UserDto
import com.airline.checkin.domain.model.User

object UserMapper {
    fun UserDto.toDomain(): User =
            User(
                    id = id,
                    fullName = fullName,
                    email = email,
                    phone = phone,
                    avatarUrl = avatarUrl,
                    googleId = googleId,
                    createdAt = createdAt,
                    updatedAt = updatedAt
            )
}
