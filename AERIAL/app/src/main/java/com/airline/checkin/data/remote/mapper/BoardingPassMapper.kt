package com.airline.checkin.data.remote.mapper

import com.airline.checkin.data.remote.dto.BoardingPassDto
import com.airline.checkin.domain.model.BoardingPass

object BoardingPassMapper {
    fun toDomain(dto: BoardingPassDto): BoardingPass = BoardingPass(dto.id, dto.checkInId)
}
