package com.airline.checkin.data.local.room.converters

import androidx.room.TypeConverter
import com.airline.checkin.data.local.room.entity.BookingStatus
import com.airline.checkin.data.local.room.entity.FlightStatus
import com.airline.checkin.data.local.room.entity.NotificationStatus
import com.airline.checkin.data.local.room.entity.NotificationType
import com.airline.checkin.data.local.room.entity.PassengerType
import com.airline.checkin.data.local.room.entity.SeatClass
import com.airline.checkin.data.local.room.entity.SeatType

class Converters {

    @TypeConverter fun fromFlightStatus(v: FlightStatus): String = v.name
    @TypeConverter fun toFlightStatus(v: String): FlightStatus = FlightStatus.valueOf(v)

    @TypeConverter fun fromBookingStatus(v: BookingStatus): String = v.name
    @TypeConverter fun toBookingStatus(v: String): BookingStatus = BookingStatus.valueOf(v)

    @TypeConverter fun fromPassengerType(v: PassengerType): String = v.name
    @TypeConverter fun toPassengerType(v: String): PassengerType = PassengerType.valueOf(v)

    @TypeConverter fun fromSeatClass(v: SeatClass): String = v.name
    @TypeConverter fun toSeatClass(v: String): SeatClass = SeatClass.valueOf(v)

    @TypeConverter fun fromSeatType(v: SeatType): String = v.name
    @TypeConverter fun toSeatType(v: String): SeatType = SeatType.valueOf(v)

    @TypeConverter fun fromNotificationType(v: NotificationType): String = v.name
    @TypeConverter fun toNotificationType(v: String): NotificationType = NotificationType.valueOf(v)

    @TypeConverter fun fromNotificationStatus(v: NotificationStatus): String = v.name
    @TypeConverter
    fun toNotificationStatus(v: String): NotificationStatus = NotificationStatus.valueOf(v)
}
