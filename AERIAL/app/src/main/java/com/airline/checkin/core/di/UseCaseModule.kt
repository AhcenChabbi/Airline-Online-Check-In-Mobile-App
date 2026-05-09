package com.airline.checkin.core.di

import com.airline.checkin.domain.usecase.auth.LoginUseCase
import com.airline.checkin.domain.usecase.auth.RegisterUseCase
import com.airline.checkin.domain.usecase.boarding.GenerateBoardingPassUseCase
import com.airline.checkin.domain.usecase.boarding.GetOfflineBoardingPassUseCase
import com.airline.checkin.domain.usecase.checkin.DeclareBaggageUseCase
import com.airline.checkin.domain.usecase.checkin.SelectSeatUseCase
import com.airline.checkin.domain.usecase.checkin.StartCheckInUseCase
import com.airline.checkin.domain.usecase.checkin.SubmitSpecialRequestUseCase
import com.airline.checkin.domain.usecase.flight.GetFlightByBookingUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideLoginUseCase(): LoginUseCase = LoginUseCase()

    @Provides
    fun provideRegisterUseCase(): RegisterUseCase = RegisterUseCase()

    @Provides
    fun provideGetFlightByBookingUseCase(): GetFlightByBookingUseCase =
        GetFlightByBookingUseCase()

    @Provides
    fun provideStartCheckInUseCase(): StartCheckInUseCase =
        StartCheckInUseCase()

    @Provides
    fun provideSelectSeatUseCase(): SelectSeatUseCase =
        SelectSeatUseCase()

    @Provides
    fun provideDeclareBaggageUseCase(): DeclareBaggageUseCase =
        DeclareBaggageUseCase()

    @Provides
    fun provideSubmitSpecialRequestUseCase(): SubmitSpecialRequestUseCase =
        SubmitSpecialRequestUseCase()

    @Provides
    fun provideGenerateBoardingPassUseCase(): GenerateBoardingPassUseCase =
        GenerateBoardingPassUseCase()

    @Provides
    fun provideGetOfflineBoardingPassUseCase(): GetOfflineBoardingPassUseCase =
        GetOfflineBoardingPassUseCase()
}
