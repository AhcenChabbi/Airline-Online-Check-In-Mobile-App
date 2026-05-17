package com.airline.checkin.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.airline.checkin.data.local.room.converters.Converters
import com.airline.checkin.data.local.room.dao.BoardingPassDao
import com.airline.checkin.data.local.room.dao.BookingDao
import com.airline.checkin.data.local.room.dao.FlightDao
import com.airline.checkin.data.local.room.dao.NotificationDao
import com.airline.checkin.data.local.room.dao.PassengerDao
import com.airline.checkin.data.local.room.dao.SeatDao
import com.airline.checkin.data.local.room.entity.BoardingPassEntity
import com.airline.checkin.data.local.room.entity.BookingEntity
import com.airline.checkin.data.local.room.entity.FlightEntity
import com.airline.checkin.data.local.room.entity.NotificationEntity
import com.airline.checkin.data.local.room.entity.PassengerEntity
import com.airline.checkin.data.local.room.entity.SeatEntity

@Database(
        entities =
                [
                        FlightEntity::class,
                        BookingEntity::class,
                        PassengerEntity::class,
                        SeatEntity::class,
                        BoardingPassEntity::class,
                        NotificationEntity::class],
        version = 1,
        exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun flightDao(): FlightDao
    abstract fun bookingDao(): BookingDao
    abstract fun passengerDao(): PassengerDao
    abstract fun seatDao(): SeatDao
    abstract fun boardingPassDao(): BoardingPassDao
    abstract fun notificationDao(): NotificationDao
}
