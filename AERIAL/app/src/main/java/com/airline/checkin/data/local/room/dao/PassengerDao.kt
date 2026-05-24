package com.airline.checkin.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.airline.checkin.data.local.room.entity.PassengerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PassengerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun save(passenger: PassengerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAll(passengers: List<PassengerEntity>)

    @Query("SELECT * FROM passengers WHERE id = :id") fun get(id: String): Flow<PassengerEntity?>

    @Query("SELECT * FROM passengers WHERE booking_id = :bookingId")
    fun getByBooking(bookingId: String): Flow<List<PassengerEntity>>

    @Query("SELECT * FROM passengers WHERE booking_id = :bookingId AND is_primary = 1 LIMIT 1")
    suspend fun getPrimary(bookingId: String): PassengerEntity?

    @Query("DELETE FROM passengers WHERE booking_id = :bookingId")
    suspend fun deleteByBooking(bookingId: String)
}
