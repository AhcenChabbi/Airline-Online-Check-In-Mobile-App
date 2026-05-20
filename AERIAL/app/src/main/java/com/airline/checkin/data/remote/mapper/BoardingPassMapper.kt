package com.airline.checkin.data.remote.mapper

import com.airline.checkin.data.remote.dto.BoardingPassDto
import com.airline.checkin.data.remote.dto.BoardingPassOfflinePayloadDto
import com.airline.checkin.domain.model.BoardingPass
import com.airline.checkin.domain.model.BoardingPassFlightPayload
import com.airline.checkin.domain.model.BoardingPassOfflinePayload
import com.airline.checkin.domain.model.BoardingPassPassengerPayload
import com.airline.checkin.domain.model.BoardingPassSeatPayload

object BoardingPassMapper {
    fun toDomain(dto: BoardingPassDto): BoardingPass =
            BoardingPass(
                    id = dto.id,
                    checkinId = dto.checkinId,
                    passengerId = dto.passengerId,
                    seatId = dto.seatId,
                    qrCodeData = dto.qrCodeData,
                    qrCodeUrl = dto.qrCodeUrl,
                    pdfUrl = dto.pdfUrl,
                    isSynced = dto.isSynced,
                    syncedAt = dto.syncedAt,
                    issuedAt = dto.issuedAt,
                    expiresAt = dto.expiresAt,
                    offlinePayload = dto.offlinePayload?.toDomain()
            )

    private fun BoardingPassOfflinePayloadDto.toDomain(): BoardingPassOfflinePayload =
            BoardingPassOfflinePayload(
                    passenger =
                            BoardingPassPassengerPayload(
                                    firstName = passenger.firstName,
                                    lastName = passenger.lastName,
                                    passportNumber = passenger.passportNumber
                            ),
                    flight =
                            BoardingPassFlightPayload(
                                    flightNumber = flight.flightNumber,
                                    origin = flight.origin,
                                    destination = flight.destination,
                                    departureAt = flight.departureAt,
                                    arrivalAt = flight.arrivalAt
                            ),
                    seat =
                            BoardingPassSeatPayload(
                                    seatCode = seat.seatCode,
                                    seatClass = seat.seatClass
                            )
            )
}
