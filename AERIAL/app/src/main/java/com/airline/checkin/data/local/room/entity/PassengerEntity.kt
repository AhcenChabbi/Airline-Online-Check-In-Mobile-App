package com.airline.checkin.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class PassengerType {
    ADULT,
    CHILD,
    INFANT
}

@Entity(
        tableName = "passengers",
        foreignKeys =
                [
                        ForeignKey(
                                entity = BookingEntity::class,
                                parentColumns = ["id"],
                                childColumns = ["booking_id"],
                                onDelete = ForeignKey.CASCADE
                        )],
        indices = [Index(value = ["booking_id"])]
)
data class PassengerEntity(
        @PrimaryKey @ColumnInfo(name = "id") val id: String,
        @ColumnInfo(name = "booking_id") val bookingId: String,
        @ColumnInfo(name = "first_name") val firstName: String,
        @ColumnInfo(name = "last_name") val lastName: String,
        @ColumnInfo(name = "passenger_type") val passengerType: PassengerType = PassengerType.ADULT,
        @ColumnInfo(name = "is_primary") val isPrimary: Boolean = false,
        @ColumnInfo(name = "cached_at") val cachedAt: Long = System.currentTimeMillis()
)
