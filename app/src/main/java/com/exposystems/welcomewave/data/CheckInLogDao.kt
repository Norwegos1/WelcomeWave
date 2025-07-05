package com.exposystems.welcomewave.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CheckInLogDao {
    @Insert
    suspend fun insert(log: CheckInLog)

    @Query("SELECT * FROM check_in_logs WHERE checkOutTime IS NULL ORDER BY checkInTime DESC")
    fun getCurrentlyCheckedIn(): Flow<List<CheckInLog>>

    @Query("UPDATE check_in_logs SET checkOutTime = :time WHERE id = :id")
    suspend fun setCheckOutTime(id: Int, time: Long)

    // In CheckInLogDao.kt
    @Query("SELECT * FROM check_in_logs ORDER BY checkInTime DESC")
    fun getAllLogs(): Flow<List<CheckInLog>>
}