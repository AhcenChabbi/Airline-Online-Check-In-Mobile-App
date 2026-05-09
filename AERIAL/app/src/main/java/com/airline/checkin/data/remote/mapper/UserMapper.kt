package com.airline.checkin.data.remote.mapper

import com.airline.checkin.data.remote.dto.UserDto
import com.airline.checkin.domain.model.User

object UserMapper {
    fun toDomain(dto: UserDto): User = User(dto.id, dto.name)
}
