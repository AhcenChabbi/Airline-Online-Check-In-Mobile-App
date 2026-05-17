package com.airline.checkin.data.local.room.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import com.airline.checkin.data.local.room.entity.BoardingPassEntity
import com.airline.checkin.data.local.room.entity.PassengerEntity
import com.airline.checkin.data.local.room.entity.SeatEntity
import kotlinx.coroutines.flow.Flow

data class BoardingPassWithDetails(
        @Embedded val boardingPass: BoardingPassEntity,
        @Relation(parentColumn = "passenger_id", entityColumn = "id") val passenger: PassengerEntity,
        @Relation(parentColumn = "seat_id", entityColumn = "id") val seat: SeatEntity
) // join des 3 tables pour un accès facile aux données complètes du BP

@Dao
interface BoardingPassDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(boardingPass: BoardingPassEntity)

    @Query("SELECT * FROM boarding_passes WHERE passenger_id = :passengerId LIMIT 1")
    fun getByPassenger(passengerId: String): Flow<BoardingPassEntity?>

    @Transaction
    @Query("SELECT * FROM boarding_passes WHERE passenger_id = :passengerId LIMIT 1")
    fun getWithDetails(passengerId: String): Flow<BoardingPassWithDetails?>

    @Query("SELECT * FROM boarding_passes WHERE is_synced = 0") 
    suspend fun getUnsynced(): List<BoardingPassEntity>
 
    @Query("UPDATE boarding_passes SET is_synced = 1, synced_at = :at WHERE id = :id")
    suspend fun markSynced(id: String, at: Long = System.currentTimeMillis())

    @Query("UPDATE boarding_passes SET qr_code_url = :url WHERE id = :id")
    suspend fun updateQrUrl(id: String, url: String)

    @Query("UPDATE boarding_passes SET pdf_url = :url WHERE id = :id")
    suspend fun updatePdfUrl(id: String, url: String)

    @Query("DELETE FROM boarding_passes WHERE expires_at < :now")
    suspend fun deleteExpired(now: Long = System.currentTimeMillis())
}
