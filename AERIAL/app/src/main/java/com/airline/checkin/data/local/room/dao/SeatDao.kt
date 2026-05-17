package com.airline.checkin.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.airline.checkin.data.local.room.entity.SeatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SeatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun save(seat: SeatEntity)

    @Query("SELECT * FROM seats WHERE id = :id") suspend fun get(id: String): SeatEntity?

    @Query(
            "SELECT * FROM seats WHERE flight_id = :flightId ORDER BY row_number ASC, column_letter ASC"
    )
    fun getByFlight(flightId: String): Flow<List<SeatEntity>>

    @Query("DELETE FROM seats WHERE flight_id = :flightId")
    suspend fun deleteByFlight(flightId: String)
}
