package com.exposystems.welcomewave.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "employees")
data class Employee(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // This line is crucial
    val name: String,
    val email: String,
    val title: String,
    val photoUri: String? = null
)