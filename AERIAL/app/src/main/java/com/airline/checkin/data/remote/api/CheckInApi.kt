package com.airline.checkin.data.remote.api

import com.airline.checkin.data.remote.dto.BaggageRequestDto
import com.airline.checkin.data.remote.dto.BaggageResponseDto
import com.airline.checkin.data.remote.dto.BoardingPassResponseDto
import com.airline.checkin.data.remote.dto.CheckInDetailsResponseDto
import com.airline.checkin.data.remote.dto.CheckInResponseDto
import com.airline.checkin.data.remote.dto.InitiateCheckInRequestDto
import com.airline.checkin.data.remote.dto.MessageResponseDto
import com.airline.checkin.data.remote.dto.PassportScanRequestDto
import com.airline.checkin.data.remote.dto.SeatMapResponseDto
import com.airline.checkin.data.remote.dto.SeatSelectionRequestDto
import com.airline.checkin.data.remote.dto.SeatSelectionResponseDto
import com.airline.checkin.data.remote.dto.SpecialRequestsRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CheckInApi {
    @POST("api/checkin/initiate")
    suspend fun initiateCheckIn(@Body body: InitiateCheckInRequestDto): CheckInResponseDto

    @POST("api/checkin/{checkinId}/passport")
    suspend fun submitPassport(
            @Path("checkinId") checkinId: String,
            @Body body: PassportScanRequestDto
    ): CheckInResponseDto

    @POST("api/checkin/{checkinId}/details/confirm")
    suspend fun confirmDetails(@Path("checkinId") checkinId: String): CheckInDetailsResponseDto

    @GET("api/checkin/{checkinId}/seats")
    suspend fun getSeatMap(@Path("checkinId") checkinId: String): SeatMapResponseDto

    @POST("api/checkin/{checkinId}/seats/select")
    suspend fun selectSeat(
            @Path("checkinId") checkinId: String,
            @Body body: SeatSelectionRequestDto
    ): SeatSelectionResponseDto

    @POST("api/checkin/{checkinId}/baggage")
    suspend fun declareBaggage(
            @Path("checkinId") checkinId: String,
            @Body body: BaggageRequestDto
    ): BaggageResponseDto

    @POST("api/checkin/{checkinId}/special-requests")
    suspend fun submitSpecialRequests(
            @Path("checkinId") checkinId: String,
            @Body body: SpecialRequestsRequestDto
    ): MessageResponseDto

    @POST("api/checkin/{checkinId}/confirm")
    suspend fun confirmCheckIn(@Path("checkinId") checkinId: String): BoardingPassResponseDto
}
