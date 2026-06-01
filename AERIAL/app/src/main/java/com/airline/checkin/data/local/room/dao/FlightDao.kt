package com.airline.checkin.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.airline.checkin.data.local.room.entity.FlightEntity
import com.airline.checkin.data.local.room.entity.FlightStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface FlightDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(flight: FlightEntity): Long

    @Query("SELECT * FROM flights WHERE id = :id") fun get(id: String): Flow<FlightEntity?>

    @Query("SELECT * FROM flights WHERE flight_number = :number LIMIT 1")
    suspend fun getByNumber(number: String): FlightEntity?

    @Query("UPDATE flights SET status = :status WHERE id = :flightId")
    suspend fun updateStatus(flightId: String, status: FlightStatus): Int

    @Query("DELETE FROM flights WHERE id = :id")
    suspend fun delete(id: String): Int
    
}
