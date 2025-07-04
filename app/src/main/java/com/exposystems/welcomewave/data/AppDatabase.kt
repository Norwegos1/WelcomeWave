package com.exposystems.welcomewave.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Employee::class, CheckInLog::class], version = 2, exportSchema = false) // Add CheckInLog and increment version
abstract class AppDatabase : RoomDatabase() {
    abstract fun employeeDao(): EmployeeDao
    abstract fun checkInLogDao(): CheckInLogDao // Add this line
}