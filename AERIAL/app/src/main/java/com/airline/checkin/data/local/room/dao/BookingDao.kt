package com.airline.checkin.data.local.room.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import com.airline.checkin.data.local.room.entity.BookingEntity
import com.airline.checkin.data.local.room.entity.BookingStatus
import com.airline.checkin.data.local.room.entity.FlightEntity
import kotlinx.coroutines.flow.Flow

data class BookingWithFlight(
        @Embedded val booking: BookingEntity,
        @Relation(parentColumn = "flight_id", entityColumn = "id") val flight: FlightEntity
) // join pour accéder facilement aux infos de vol associées à une réservation

@Dao
interface BookingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun save(booking: BookingEntity)

    @Query("SELECT * FROM bookings WHERE id = :id") fun get(id: String): Flow<BookingEntity?>

    @Query(
            "SELECT * FROM bookings WHERE booking_reference = :ref AND last_name = :lastName LIMIT 1"
    )
    suspend fun getByPnr(ref: String, lastName: String): BookingEntity?

    @Transaction
    @Query("SELECT * FROM bookings WHERE id = :id")
    fun getWithFlight(id: String): Flow<BookingWithFlight?>

    @Query("SELECT * FROM bookings ORDER BY cached_at DESC") fun getAll(): Flow<List<BookingEntity>>

    @Query("UPDATE bookings SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: BookingStatus)

    @Query("DELETE FROM bookings WHERE id = :id") suspend fun delete(id: String)

    @Query("DELETE FROM bookings") suspend fun clearAll()
}
