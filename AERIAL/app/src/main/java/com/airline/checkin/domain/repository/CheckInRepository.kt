package com.airline.checkin.domain.repository

import com.airline.checkin.domain.model.Baggage
import com.airline.checkin.domain.model.BoardingPass
import com.airline.checkin.domain.model.CheckIn
import com.airline.checkin.domain.model.Flight
import com.airline.checkin.domain.model.Passenger
import com.airline.checkin.domain.model.PassportScanData
import com.airline.checkin.domain.model.Seat
import com.airline.checkin.domain.model.SpecialRequest

interface CheckInRepository {
    suspend fun initiateCheckIn(bookingId: String, passengerId: String): Result<CheckIn>
    suspend fun submitPassport(checkinId: String, data: PassportScanData): Result<CheckIn>
    suspend fun confirmDetails(checkinId: String): Result<Pair<Passenger, Flight>>
    suspend fun getSeatMap(checkinId: String): Result<List<Seat>>
    suspend fun selectSeat(checkinId: String, seatId: String): Result<Seat>
    suspend fun declareBaggage(checkinId: String, bags: List<Baggage>): Result<Int>
    suspend fun submitSpecialRequests(checkinId: String, requests: List<SpecialRequest>): Result<String>
    suspend fun confirmCheckIn(checkinId: String): Result<BoardingPass>
}
