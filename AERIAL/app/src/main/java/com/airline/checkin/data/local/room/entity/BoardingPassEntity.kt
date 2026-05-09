package com.airline.checkin.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity data class BoardingPassEntity(@PrimaryKey val id: String, val checkInId: String)
