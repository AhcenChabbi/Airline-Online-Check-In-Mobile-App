package com.airline.checkin.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
        tableName = "boarding_passes",
        foreignKeys =
                [
                        ForeignKey(
                                entity = PassengerEntity::class,
                                parentColumns = ["id"],
                                childColumns = ["passenger_id"],
                                onDelete = ForeignKey.CASCADE
                        ),
                        ForeignKey(
                                entity = SeatEntity::class,
                                parentColumns = ["id"],
                                childColumns = ["seat_id"],
                                onDelete = ForeignKey.CASCADE
                        )],
        indices =
                [
                        Index(value = ["passenger_id"], unique = true),
                        Index(value = ["seat_id"], unique = true),
                        Index(value = ["qr_code_data"], unique = true)]
)
data class BoardingPassEntity(
        @PrimaryKey @ColumnInfo(name = "id") val id: String,
        @ColumnInfo(name = "passenger_id") val passengerId: String,
        @ColumnInfo(name = "seat_id") val seatId: String,
        @ColumnInfo(name = "qr_code_data") val qrCodeData: String,
        @ColumnInfo(name = "qr_code_url") val qrCodeUrl: String? = null,
        @ColumnInfo(name = "pdf_url") val pdfUrl: String? = null,
        @ColumnInfo(name = "issued_at") val issuedAt: Long = System.currentTimeMillis(),
        @ColumnInfo(name = "expires_at") val expiresAt: Long,

        // Full JSON snapshot: flight + passenger + seat — render offline with zero extra queries
        @ColumnInfo(name = "offline_payload") val offlinePayload: String,
        @ColumnInfo(name = "is_synced") val isSynced: Boolean = false,
        @ColumnInfo(name = "synced_at") val syncedAt: Long? = null
)
 