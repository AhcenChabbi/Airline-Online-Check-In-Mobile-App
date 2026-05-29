package com.airline.checkin.core.di

import com.airline.checkin.domain.usecase.auth.LoginUseCase
import com.airline.checkin.domain.usecase.auth.RegisterUseCase
import com.airline.checkin.domain.usecase.boarding.DownloadBoardingPassPdfUseCase
import com.airline.checkin.domain.usecase.boarding.GenerateBoardingPassUseCase
import com.airline.checkin.domain.usecase.boarding.GetBoardingPassUseCase
import com.airline.checkin.domain.usecase.boarding.GetOfflineBoardingPassUseCase
import com.airline.checkin.domain.usecase.checkin.ConfirmDetailsUseCase
import com.airline.checkin.domain.usecase.checkin.DeclareBaggageUseCase
import com.airline.checkin.domain.usecase.checkin.GetSeatMapUseCase
import com.airline.checkin.domain.usecase.checkin.ScanPassportUseCase
import com.airline.checkin.domain.usecase.checkin.SelectSeatUseCase
import com.airline.checkin.domain.usecase.checkin.StartCheckInUseCase
import com.airline.checkin.domain.usecase.checkin.SubmitSpecialRequestUseCase
import com.airline.checkin.domain.usecase.flight.GetFlightByBookingUseCase
import com.airline.checkin.domain.repository.BoardingPassRepository
import com.airline.checkin.domain.repository.CheckInRepository
import com.airline.checkin.domain.repository.FlightRepository
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
    fun provideGetFlightByBookingUseCase(
        flightRepository: FlightRepository
    ): GetFlightByBookingUseCase = GetFlightByBookingUseCase(flightRepository)

    @Provides
    fun provideStartCheckInUseCase(
        checkInRepository: CheckInRepository
    ): StartCheckInUseCase = StartCheckInUseCase(checkInRepository)

    @Provides
    fun provideScanPassportUseCase(
        checkInRepository: CheckInRepository
    ): ScanPassportUseCase = ScanPassportUseCase(checkInRepository)

    @Provides
    fun provideConfirmDetailsUseCase(
        checkInRepository: CheckInRepository
    ): ConfirmDetailsUseCase = ConfirmDetailsUseCase(checkInRepository)

    @Provides
    fun provideGetSeatMapUseCase(
        checkInRepository: CheckInRepository
    ): GetSeatMapUseCase = GetSeatMapUseCase(checkInRepository)

    @Provides
    fun provideSelectSeatUseCase(
        checkInRepository: CheckInRepository
    ): SelectSeatUseCase = SelectSeatUseCase(checkInRepository)

    @Provides
    fun provideDeclareBaggageUseCase(
        checkInRepository: CheckInRepository
    ): DeclareBaggageUseCase = DeclareBaggageUseCase(checkInRepository)

    @Provides
    fun provideSubmitSpecialRequestUseCase(
        checkInRepository: CheckInRepository
    ): SubmitSpecialRequestUseCase = SubmitSpecialRequestUseCase(checkInRepository)

    @Provides
    fun provideGenerateBoardingPassUseCase(
        checkInRepository: CheckInRepository
    ): GenerateBoardingPassUseCase = GenerateBoardingPassUseCase(checkInRepository)

    @Provides
    fun provideGetOfflineBoardingPassUseCase(
        boardingPassRepository: BoardingPassRepository
    ): GetOfflineBoardingPassUseCase = GetOfflineBoardingPassUseCase(boardingPassRepository)

    @Provides
    fun provideGetBoardingPassUseCase(
        boardingPassRepository: BoardingPassRepository
    ): GetBoardingPassUseCase = GetBoardingPassUseCase(boardingPassRepository)

    @Provides
    fun provideDownloadBoardingPassPdfUseCase(
        boardingPassRepository: BoardingPassRepository
    ): DownloadBoardingPassPdfUseCase = DownloadBoardingPassPdfUseCase(boardingPassRepository)
}
