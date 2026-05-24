package com.airline.checkin.data.remote.api

import com.airline.checkin.data.remote.dto.BookingLookupRequestDto
import com.airline.checkin.data.remote.dto.BookingLookupResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface FlightApi {
    @POST("api/bookings/lookup")
    suspend fun lookupBooking(@Body body: BookingLookupRequestDto): BookingLookupResponseDto
}
