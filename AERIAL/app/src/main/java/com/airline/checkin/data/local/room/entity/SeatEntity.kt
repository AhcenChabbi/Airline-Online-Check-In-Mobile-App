package com.airline.checkin.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class SeatClass {
    ECONOMY,
    BUSINESS,
    FIRST
}

enum class SeatType {
    STANDARD,
    WINDOW,
    AISLE,
    MIDDLE,
    EXTRA_LEGROOM,
    EMERGENCY_EXIT
}

@Entity(
        tableName = "seats",
        foreignKeys =
                [
                        ForeignKey(
                                entity = FlightEntity::class,
                                parentColumns = ["id"],
                                childColumns = ["flight_id"],
                                onDelete = ForeignKey.CASCADE
                        )],
        indices =
                [
                        Index(value = ["flight_id", "seat_code"], unique = true),
                        Index(value = ["flight_id"])]
)
data class SeatEntity(
        @PrimaryKey @ColumnInfo(name = "id") val id: String,
        @ColumnInfo(name = "flight_id") val flightId: String,
        @ColumnInfo(name = "seat_code") val seatCode: String,
        @ColumnInfo(name = "row_number") val rowNumber: Int,
        @ColumnInfo(name = "column_letter") val columnLetter: String,
        @ColumnInfo(name = "class") val seatClass: SeatClass = SeatClass.ECONOMY,
        @ColumnInfo(name = "type") val type: SeatType = SeatType.STANDARD
)
