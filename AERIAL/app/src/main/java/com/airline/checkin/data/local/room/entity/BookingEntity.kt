package com.airline.checkin.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class BookingStatus {
    PENDING,
    CHECKIN_OPEN,
    CHECKED_IN,
    CANCELLED
}

@Entity(
        tableName = "bookings",
        foreignKeys =
                [
                        ForeignKey(
                                entity = FlightEntity::class,
                                parentColumns = ["id"],
                                childColumns = ["flight_id"],
                                onDelete = ForeignKey.CASCADE
                        )],
        indices =
                [Index(value = ["booking_reference"], unique = true), Index(value = ["flight_id"])]
)
data class BookingEntity(
        @PrimaryKey @ColumnInfo(name = "id") val id: String,
        @ColumnInfo(name = "flight_id") val flightId: String,
        @ColumnInfo(name = "booking_reference") val bookingReference: String,
        @ColumnInfo(name = "last_name") val lastName: String,
        @ColumnInfo(name = "status") val status: BookingStatus = BookingStatus.PENDING,
        @ColumnInfo(name = "cached_at") val cachedAt: Long = System.currentTimeMillis()
)
