package com.airline.checkin.data.remote.dto

data class InitiateCheckInRequestDto(val bookingId: String, val passengerId: String)

data class PassportScanRequestDto(
        val passportNumber: String,
        val passportExpiry: String,
        val passportMrz: String? = null,
        val passportScanUrl: String? = null
)

data class SeatSelectionRequestDto(val seatId: String)

data class BaggageItemDto(val bagType: String, val quantity: Int, val weightKg: Double? = null)

data class BaggageRequestDto(val bags: List<BaggageItemDto>)

data class SpecialRequestItemDto(
        val category: String,
        val detail: String,
        val notes: String? = null
)

data class SpecialRequestsRequestDto(val requests: List<SpecialRequestItemDto>)

data class CheckInResponseDto(val checkin: CheckInDto)

data class CheckInDetailsResponseDto(val passenger: PassengerDto, val flight: FlightDto)

data class SeatMapResponseDto(val seats: List<SeatDto>)

data class SeatSelectionResponseDto(val seat: SeatDto)

data class BaggageResultDto(val count: Int)

data class BaggageResponseDto(val result: BaggageResultDto)

data class BoardingPassResponseDto(val boardingPass: BoardingPassDto)

data class BoardingPassDataResponseDto(val success: Boolean, val data: BoardingPassDto)
