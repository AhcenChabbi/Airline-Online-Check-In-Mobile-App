package com.airline.checkin.core.di

import android.content.Context
import androidx.room.Room
import com.airline.checkin.data.local.room.AppDatabase
import com.airline.checkin.data.local.room.dao.BoardingPassDao
import com.airline.checkin.data.local.room.dao.BookingDao
import com.airline.checkin.data.local.room.dao.FlightDao
import com.airline.checkin.data.local.room.dao.NotificationDao
import com.airline.checkin.data.local.room.dao.PassengerDao
import com.airline.checkin.data.local.room.dao.SeatDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module //Voici comment créer un objet que tu ne peux pas construire tout seul comme :interface ,Dao, Retrofit, RoomDatabase
@InstallIn(SingletonComponent::class) //Indique que les dépendances fournies par ce module seront disponibles à l'échelle de l'application (singleton)
object RoomModule {

    @Provides //Indique que cette méthode fournit une instance de AppDatabase
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "checkin_offline.db")
                    .fallbackToDestructiveMigration()
                    .build()

    @Provides fun provideFlightDao(db: AppDatabase): FlightDao = db.flightDao()

    @Provides fun provideBookingDao(db: AppDatabase): BookingDao = db.bookingDao()

    @Provides fun providePassengerDao(db: AppDatabase): PassengerDao = db.passengerDao()

    @Provides fun provideSeatDao(db: AppDatabase): SeatDao = db.seatDao()

    @Provides fun provideBoardingPassDao(db: AppDatabase): BoardingPassDao = db.boardingPassDao()

    @Provides fun provideNotificationDao(db: AppDatabase): NotificationDao = db.notificationDao()
}
