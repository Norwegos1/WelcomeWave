package com.exposystems.welcomewave.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Employee(
    @DocumentId
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val title: String? = null,
    val photoUrl: String? = null,
    val department: String? = null,
    val isactive: Boolean = true,
    val createdAt: Date? = null,
    @ServerTimestamp
    val updatedAt: Date? = null
)