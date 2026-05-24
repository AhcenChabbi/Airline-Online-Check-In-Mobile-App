package com.airline.checkin.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class FlightStatus {
    SCHEDULED,
    BOARDING,
    DEPARTED,
    ARRIVED,
    CANCELLED,
    DELAYED
}

@Entity(tableName = "flights", indices = [Index(value = ["flight_number"], unique = true)])
data class FlightEntity(
        @PrimaryKey @ColumnInfo(name = "id") val id: String,
        @ColumnInfo(name = "flight_number") val flightNumber: String,
        @ColumnInfo(name = "airline_code") val airlineCode: String,
        @ColumnInfo(name = "airline_name") val airlineName: String = "",
        @ColumnInfo(name = "origin_iata") val originIata: String,
        @ColumnInfo(name = "origin_city") val originCity: String = "",
        @ColumnInfo(name = "destination_iata") val destIata: String,
        @ColumnInfo(name = "destination_city") val destinationCity: String = "",
        @ColumnInfo(name = "departure_at") val departureAt: Long,
        @ColumnInfo(name = "arrival_at") val arrivalAt: Long,
        @ColumnInfo(name = "aircraft_type") val aircraftType: String,
        @ColumnInfo(name = "status") val status: FlightStatus = FlightStatus.SCHEDULED,
        @ColumnInfo(name = "cached_at") val cachedAt: Long = System.currentTimeMillis()
)
