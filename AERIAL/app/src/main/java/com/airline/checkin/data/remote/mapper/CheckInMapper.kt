package com.airline.checkin.data.remote.mapper

import com.airline.checkin.data.remote.dto.CheckInDto
import com.airline.checkin.domain.model.CheckIn

object CheckInMapper {
    fun toDomain(dto: CheckInDto): CheckIn =
            CheckIn(
                    id = dto.id,
                    bookingId = dto.bookingId,
                    passengerId = dto.passengerId,
                    status = dto.status,
                    currentStep = dto.currentStep,
                    startedAt = dto.startedAt ?: "",
                    completedAt = dto.completedAt,
                    ipAddress = dto.ipAddress
            )
}
