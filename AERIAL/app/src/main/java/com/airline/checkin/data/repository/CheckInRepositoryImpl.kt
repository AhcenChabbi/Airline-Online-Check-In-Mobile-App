package com.airline.checkin.data.repository

import com.airline.checkin.data.remote.api.CheckInApi
import com.airline.checkin.data.remote.dto.InitiateCheckInRequestDto
import com.airline.checkin.data.remote.dto.PassportScanRequestDto
import com.airline.checkin.data.remote.dto.SeatSelectionRequestDto
import com.airline.checkin.data.remote.mapper.BaggageMapper
import com.airline.checkin.data.remote.mapper.BoardingPassMapper
import com.airline.checkin.data.remote.mapper.CheckInMapper
import com.airline.checkin.data.remote.mapper.FlightMapper
import com.airline.checkin.data.remote.mapper.PassengerMapper
import com.airline.checkin.data.remote.mapper.SeatMapper
import com.airline.checkin.data.remote.mapper.SpecialRequestMapper
import com.airline.checkin.domain.model.Baggage
import com.airline.checkin.domain.model.BoardingPass
import com.airline.checkin.domain.model.CheckIn
import com.airline.checkin.domain.model.Flight
import com.airline.checkin.domain.model.Passenger
import com.airline.checkin.domain.model.PassportScanData
import com.airline.checkin.domain.model.Seat
import com.airline.checkin.domain.model.SpecialRequest
import com.airline.checkin.domain.repository.CheckInRepository
import javax.inject.Inject

class CheckInRepositoryImpl @Inject constructor(
    private val checkInApi: CheckInApi
) : CheckInRepository {
    override suspend fun initiateCheckIn(
        bookingId: String,
        passengerId: String
    ): Result<CheckIn> = runCatching {
        val response = checkInApi.initiateCheckIn(
            InitiateCheckInRequestDto(bookingId = bookingId, passengerId = passengerId)
        )
        CheckInMapper.toDomain(response.checkin)
    }

    override suspend fun submitPassport(
        checkinId: String,
        data: PassportScanData
    ): Result<CheckIn> = runCatching {
        val response = checkInApi.submitPassport(
            checkinId,
            PassportScanRequestDto(
                passportNumber = data.passportNumber,
                passportExpiry = data.passportExpiry.toApiDateString(),
                passportMrz = data.passportMrz,
                passportScanUrl = data.passportScanUrl
            )
        )
        CheckInMapper.toDomain(response.checkin)
    }

    override suspend fun confirmDetails(checkinId: String): Result<Pair<Passenger, Flight>> =
        runCatching {
            val response = checkInApi.confirmDetails(checkinId)
            PassengerMapper.toDomain(response.passenger) to FlightMapper.toDomain(response.flight)
        }

    override suspend fun getSeatMap(checkinId: String): Result<List<Seat>> = runCatching {
        val response = checkInApi.getSeatMap(checkinId)
        response.seats.map { SeatMapper.toDomain(it) }
    }

    override suspend fun selectSeat(checkinId: String, seatId: String): Result<Seat> =
        runCatching {
            val response = checkInApi.selectSeat(
                checkinId,
                SeatSelectionRequestDto(seatId = seatId)
            )
            SeatMapper.toDomain(response.seat)
        }

    override suspend fun declareBaggage(
        checkinId: String,
        bags: List<Baggage>
    ): Result<Int> = runCatching {
        val response = checkInApi.declareBaggage(checkinId, BaggageMapper.toRequest(bags))
        response.result.count
    }

    override suspend fun submitSpecialRequests(
        checkinId: String,
        requests: List<SpecialRequest>
    ): Result<String> = runCatching {
        val response = checkInApi.submitSpecialRequests(
            checkinId,
            SpecialRequestMapper.toRequest(requests)
        )
        response.message
    }

    override suspend fun confirmCheckIn(checkinId: String): Result<BoardingPass> = runCatching {
        val response = checkInApi.confirmCheckIn(checkinId)
        BoardingPassMapper.toDomain(response.boardingPass)
    }
}

private fun String.toApiDateString(): String =
    if (length >= 10) substring(0, 10) else this
