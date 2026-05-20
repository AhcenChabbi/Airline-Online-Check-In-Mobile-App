package com.airline.checkin.data.remote.dto

data class BookingLookupRequestDto(val bookingReference: String, val lastName: String)

data class BookingLookupResponseDto(
        val bookingId: String,
        val bookingReference: String,
        val status: String,
        val isCheckinOpen: Boolean,
        val flight: FlightSummaryDto,
        val passengers: List<PassengerSummaryDto>
)

data class FlightSummaryDto(
        val flightNumber: String,
        val airlineCode: String,
        val originIata: String,
        val destIata: String,
        val departureAt: String,
        val arrivalAt: String,
        val aircraftType: String,
        val status: String
)

data class PassengerSummaryDto(
        val id: String,
        val firstName: String,
        val lastName: String,
        val passengerType: String,
        val isPrimary: Boolean,
        val checkinStatus: String? = null,
        val currentStep: String? = null,
        val hasBoardingPass: Boolean
)
