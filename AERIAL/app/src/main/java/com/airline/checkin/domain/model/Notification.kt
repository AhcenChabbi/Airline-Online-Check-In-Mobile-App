package com.airline.checkin.domain.model

import java.util.Date

data class Notification(
        val id: String,
        val userId: String,
        val bookingId: String?,
        val type: NotificationType,
        val channel: NotificationChannel,
        val status: NotificationStatus,
        val payload: NotificationPayload? = null,
        val title: String = payload?.title ?: "",
        val body: String = payload?.body ?: "",
        val sentAt: Date?,
        val readAt: Date?
)

data class NotificationPayload(val title: String, val body: String, val deepLink: String? = null)

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
