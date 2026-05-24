package com.airline.checkin.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class NotificationType {
        CHECKIN_CONFIRMED,
        BOARDING_PASS_READY,
        GATE_CHANGE,
        FLIGHT_DELAY,
        FLIGHT_CANCELLED,
        REMINDER
}

enum class NotificationStatus {
        PENDING,
        SENT,
        DELIVERED,
        READ,
        FAILED
}

@Entity(
        tableName = "notifications",
        foreignKeys =
                [
                        ForeignKey(
                                entity = BookingEntity::class,
                                parentColumns = ["id"],
                                childColumns = ["booking_id"],
                                onDelete = ForeignKey.CASCADE
                        )],
        indices = [Index(value = ["booking_id"]), Index(value = ["is_read"])]
)
data class NotificationEntity(
        @PrimaryKey @ColumnInfo(name = "id") val id: String,
        @ColumnInfo(name = "user_id") val userId: String,
        @ColumnInfo(name = "booking_id") val bookingId: String,
        @ColumnInfo(name = "type") val type: NotificationType,
        @ColumnInfo(name = "status") val status: NotificationStatus = NotificationStatus.PENDING,
        @ColumnInfo(name = "title") val title: String,
        @ColumnInfo(name = "body") val body: String,
        @ColumnInfo(name = "is_read") val isRead: Boolean = false,
        @ColumnInfo(name = "received_at") val receivedAt: Long = System.currentTimeMillis()
)
