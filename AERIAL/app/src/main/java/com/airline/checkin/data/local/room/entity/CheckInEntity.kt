package com.airline.checkin.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity data class CheckInEntity(@PrimaryKey val id: String, val bookingId: String)
