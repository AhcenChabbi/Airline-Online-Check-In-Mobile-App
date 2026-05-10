package com.airline.checkin.data.remote.mapper

import com.airline.checkin.data.remote.dto.BoardingPassDto
import com.airline.checkin.domain.model.BoardingPass

object BoardingPassMapper {
    fun toDomain(dto: BoardingPassDto): BoardingPass =
        BoardingPass(
            id = dto.id,
            checkinId = dto.checkinId,
            passengerId = dto.passengerId,
            seatId = dto.seatId,
            qrCodeData = dto.qrCodeData,
            pdfUrl = dto.pdfUrl,
            isSynced = dto.isSynced,
            issuedAt = dto.issuedAt,
            expiresAt = dto.expiresAt,
            offlinePayload = dto.offlinePayload
        )
}
