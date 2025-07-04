package com.exposystems.welcomewave.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "check_in_logs")
data class CheckInLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val visitorCompany: String,
    val visitorNames: String, // We'll store the list as a single comma-separated string
    val employeeId: Int,
    val checkInTime: Long = System.currentTimeMillis(),
    var checkOutTime: Long? = null // Null until the guest checks out
)