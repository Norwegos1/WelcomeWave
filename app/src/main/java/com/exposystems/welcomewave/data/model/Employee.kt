package com.exposystems.welcomewave.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Employee(
    // @DocumentId tells Firestore to map the document's ID to this field.
    // When adding a new document, if 'id' is empty, Firestore generates a unique ID.
    @DocumentId
    val id: String = "",
    val firstName: String = "", // Changed 'name' to 'firstName' based on typical breakdown
    val lastName: String = "",  // Added 'lastName'
    val email: String = "",
    val title: String? = null, // Made nullable as per typical office data
    val photoUrl: String? = null, // Renamed from photoUri, nullable if photo is optional
    val department: String? = null, // Added department as discussed earlier
    val isactive: Boolean = true, // Added for managing active/inactive employees

    // @ServerTimestamp automatically populates with the server's time
    // Useful for tracking creation/last update times consistently
    @ServerTimestamp
    val createdAt: Date? = null,
    @ServerTimestamp
    val updatedAt: Date? = null
)