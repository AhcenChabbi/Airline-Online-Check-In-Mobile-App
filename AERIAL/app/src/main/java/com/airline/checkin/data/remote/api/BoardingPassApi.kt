package com.airline.checkin.data.remote.api

import com.airline.checkin.data.remote.dto.BoardingPassDataResponseDto
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming

interface BoardingPassApi {
    @GET("api/checkin/{checkinId}/boarding-pass")
    suspend fun getBoardingPass(@Path("checkinId") checkinId: String): BoardingPassDataResponseDto

    @Streaming
    @GET("api/checkin/{checkinId}/boarding-pass/pdf")
    suspend fun downloadBoardingPassPdf(@Path("checkinId") checkinId: String): ResponseBody
}
