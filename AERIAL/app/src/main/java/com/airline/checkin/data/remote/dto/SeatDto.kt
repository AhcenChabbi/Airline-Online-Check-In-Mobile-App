package com.airline.checkin.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SeatDto(
        val id: String,
        val seatCode: String,
        val rowNumber: Int,
        val columnLetter: String,
        @SerializedName("class") val seatClass: String,
        val type: String,
        val flightId: String? = null,
        val reservedByCheckinId: String? = null,
        val reservedAt: String? = null,
        val isOccupied: Boolean? = null
)
