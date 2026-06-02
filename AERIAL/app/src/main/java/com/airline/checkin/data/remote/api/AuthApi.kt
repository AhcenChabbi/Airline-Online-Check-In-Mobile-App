package com.airline.checkin.data.remote.api

import com.airline.checkin.data.remote.dto.AuthResponseDto
import com.airline.checkin.data.remote.dto.FcmTokenRequestDto
import com.airline.checkin.data.remote.dto.GoogleAuthRequestDto
import com.airline.checkin.data.remote.dto.LoginRequestDto
import com.airline.checkin.data.remote.dto.MessageResponseDto
import com.airline.checkin.data.remote.dto.RefreshTokenRequestDto
import com.airline.checkin.data.remote.dto.RegisterRequestDto
import com.airline.checkin.data.remote.dto.UserMeResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthApi {
    @Headers("No-Auth: true")
    @POST("api/auth/register")
    suspend fun register(@Body body: RegisterRequestDto): AuthResponseDto

    @Headers("No-Auth: true")
    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequestDto): AuthResponseDto

    @Headers("No-Auth: true")
    @POST("api/auth/google")
    suspend fun googleAuth(@Body body: GoogleAuthRequestDto): AuthResponseDto


    @POST("api/auth/refresh")
    suspend fun refresh(@Body body: RefreshTokenRequestDto): AuthResponseDto

    @GET("api/users/me")
    suspend fun getMe(): UserMeResponseDto

    @POST("api/users/fcm-token")
    suspend fun registerFcmToken(@Body body: FcmTokenRequestDto): MessageResponseDto
}
