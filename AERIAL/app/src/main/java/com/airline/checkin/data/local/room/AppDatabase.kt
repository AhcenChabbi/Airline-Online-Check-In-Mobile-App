package com.airline.checkin.data.local.room

import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomDatabase
import com.airline.checkin.data.local.room.dao.BoardingPassDao
import com.airline.checkin.data.local.room.dao.CheckInDao
import com.airline.checkin.data.local.room.dao.FlightDao

@Database(entities = [PlaceholderEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun flightDao(): FlightDao

    abstract fun checkInDao(): CheckInDao

    abstract fun boardingPassDao(): BoardingPassDao
}

@Entity(tableName = "placeholder_entity")
data class PlaceholderEntity(
    @PrimaryKey val id: Int = 0
)
