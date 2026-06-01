package com.airline.checkin.core.di

import com.airline.checkin.domain.usecase.auth.LoginUseCase
import com.airline.checkin.domain.usecase.auth.RegisterUseCase
import com.airline.checkin.domain.usecase.boarding.DownloadBoardingPassPdfUseCase
import com.airline.checkin.domain.usecase.boarding.GenerateBoardingPassUseCase
import com.airline.checkin.domain.usecase.boarding.GetOfflineBoardingPassUseCase
import com.airline.checkin.domain.usecase.checkin.DeclareBaggageUseCase
import com.airline.checkin.domain.usecase.checkin.SelectSeatUseCase
import com.airline.checkin.domain.usecase.checkin.StartCheckInUseCase
import com.airline.checkin.domain.usecase.checkin.SubmitSpecialRequestUseCase
import com.airline.checkin.domain.usecase.flight.GetFlightByBookingUseCase
import com.airline.checkin.domain.repository.BoardingPassRepository
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
    fun provideGenerateBoardingPassUseCase(
        repository: BoardingPassRepository
    ): GenerateBoardingPassUseCase = GenerateBoardingPassUseCase(repository)

    @Provides
    fun provideGetOfflineBoardingPassUseCase(
        repository: BoardingPassRepository
    ): GetOfflineBoardingPassUseCase = GetOfflineBoardingPassUseCase(repository)

    @Provides
    fun provideDownloadBoardingPassPdfUseCase(
        repository: BoardingPassRepository
    ): DownloadBoardingPassPdfUseCase = DownloadBoardingPassPdfUseCase(repository)
}
