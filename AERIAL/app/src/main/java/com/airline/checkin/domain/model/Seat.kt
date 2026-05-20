package com.airline.checkin.domain.model

data class Seat(
        val id: String = "",
        val flightId: String = "",
        val seatCode: String = "",
        val rowNumber: Int = 0,
        val columnLetter: String = "",
        val seatClass: String = "ECONOMY",
        val seatType: String = "STANDARD",
        val reservedByCheckinId: String? = null,
        val reservedAt: String? = null,
        val isOccupied: Boolean = false
) {
    val row: Int
        get() = rowNumber

    val column: String
        get() = columnLetter

    constructor(row: Int, column: String) : this(rowNumber = row, columnLetter = column)
}
