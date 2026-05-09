package com.airline.checkin.core.di

import com.airline.checkin.data.repository.AuthRepositoryImpl
import com.airline.checkin.data.repository.BoardingPassRepositoryImpl
import com.airline.checkin.data.repository.CheckInRepositoryImpl
import com.airline.checkin.data.repository.FlightRepositoryImpl
import com.airline.checkin.data.repository.SyncRepositoryImpl
import com.airline.checkin.domain.repository.AuthRepository
import com.airline.checkin.domain.repository.BoardingPassRepository
import com.airline.checkin.domain.repository.CheckInRepository
import com.airline.checkin.domain.repository.FlightRepository
import com.airline.checkin.domain.repository.SyncRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindFlightRepository(impl: FlightRepositoryImpl): FlightRepository

    @Binds
    @Singleton
    abstract fun bindCheckInRepository(impl: CheckInRepositoryImpl): CheckInRepository

    @Binds
    @Singleton
    abstract fun bindBoardingPassRepository(impl: BoardingPassRepositoryImpl): BoardingPassRepository

    @Binds
    @Singleton
    abstract fun bindSyncRepository(impl: SyncRepositoryImpl): SyncRepository
}
