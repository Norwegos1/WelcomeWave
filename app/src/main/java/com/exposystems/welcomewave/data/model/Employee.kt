package com.exposystems.welcomewave.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Employee(
    // @DocumentId tells Firestore to map the document's ID to this field.
    // When adding a new document, if 'id' is empty, Firestore generates a unique ID.
    @DocumentId
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val title: String? = null,
    val photoUrl: String? = null,
    val department: String? = null,
    val isactive: Boolean = true,

    // @ServerTimestamp automatically populates with the server's time
    // Useful for tracking creation/last update times consistently
    @ServerTimestamp
    val createdAt: Date? = null,
    @ServerTimestamp
    val updatedAt: Date? = null
)