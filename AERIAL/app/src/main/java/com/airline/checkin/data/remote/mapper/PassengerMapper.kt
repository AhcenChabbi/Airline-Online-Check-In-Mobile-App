package com.airline.checkin.data.remote.mapper

import com.airline.checkin.data.remote.dto.PassengerDto
import com.airline.checkin.domain.model.Passenger

object PassengerMapper {
    fun toDomain(dto: PassengerDto): Passenger =
            Passenger(
                    id = dto.id,
                    bookingId = dto.bookingId,
                    firstName = dto.firstName,
                    lastName = dto.lastName,
                    passengerType = dto.passengerType,
                    dateOfBirth = dto.dateOfBirth,
                    nationality = dto.nationality,
                    passportNumber = dto.passportNumber,
                    passportExpiry = dto.passportExpiry,
                    passportMrz = dto.passportMrz,
                    passportScanUrl = dto.passportScanUrl,
                    isPrimary = dto.isPrimary,
                    createdAt = dto.createdAt
            )
}
