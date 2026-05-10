package com.airline.checkin.domain.model

import java.util.Date

data class Notification(
    val id: String,
    val userId: String,
    val bookingId: String?,
    val type: NotificationType,
    val channel: NotificationChannel,
    val status: NotificationStatus,
    val title: String,
    val body: String,
    val sentAt: Date?,
    val readAt: Date?
)

enum class NotificationType {
    CHECKIN_CONFIRMED,
    BOARDING_PASS_READY,
    GATE_CHANGE,
    FLIGHT_DELAY,
    FLIGHT_CANCELLED,
    REMINDER
}

enum class NotificationChannel {
    PUSH,
    EMAIL,
    SMS
}

enum class NotificationStatus {
    PENDING,
    SENT,
    DELIVERED,
    READ,
    FAILED
}
