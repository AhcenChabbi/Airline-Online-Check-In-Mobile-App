package com.airline.checkin.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.airline.checkin.data.local.room.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAll(notifications: List<NotificationEntity>)

    @Query("SELECT * FROM notifications WHERE booking_id = :bookingId ORDER BY received_at DESC")
    fun getByBooking(bookingId: String): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE booking_id = :bookingId AND is_read = 0")
    fun getUnreadCount(bookingId: String): Flow<Int>

    @Query("UPDATE notifications SET is_read = 1 WHERE id = :id") suspend fun markRead(id: String)

    @Query("UPDATE notifications SET is_read = 1 WHERE booking_id = :bookingId")
    suspend fun markAllRead(bookingId: String)

    @Query("DELETE FROM notifications WHERE booking_id = :bookingId")
    suspend fun deleteByBooking(bookingId: String)
}
