package com.airline.checkin.data.remote.mapper

import com.airline.checkin.data.remote.dto.SeatDto
import com.airline.checkin.domain.model.Seat

object SeatMapper {
    fun toDomain(dto: SeatDto): Seat =
            Seat(
                    id = dto.id,
                    flightId = dto.flightId ?: "",
                    seatCode = dto.seatCode,
                    rowNumber = dto.rowNumber,
                    columnLetter = dto.columnLetter,
                    seatClass = dto.seatClass,
                    seatType = dto.type,
                    reservedByCheckinId = dto.reservedByCheckinId,
                    reservedAt = dto.reservedAt,
                    isOccupied = dto.isOccupied ?: (dto.reservedByCheckinId != null)
            )
}
