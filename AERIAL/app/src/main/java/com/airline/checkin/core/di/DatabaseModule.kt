package com.airline.checkin.core.di

import android.content.Context
import androidx.room.Room
import com.airline.checkin.data.local.room.AppDatabase
import com.airline.checkin.data.local.room.dao.BoardingPassDao
import com.airline.checkin.data.local.room.dao.CheckInDao
import com.airline.checkin.data.local.room.dao.FlightDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "aerial_db"
        ).fallbackToDestructiveMigration().build()

    @Provides
    fun provideFlightDao(db: AppDatabase): FlightDao = db.flightDao()

    @Provides
    fun provideCheckInDao(db: AppDatabase): CheckInDao = db.checkInDao()

    @Provides
    fun provideBoardingPassDao(db: AppDatabase): BoardingPassDao = db.boardingPassDao()
}
