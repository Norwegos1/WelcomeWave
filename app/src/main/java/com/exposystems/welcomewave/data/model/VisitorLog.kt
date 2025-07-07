package com.exposystems.welcomewave.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class VisitorLog(
    @DocumentId
    val id: String = "", // Document ID for this log entry
    val visitorName: String = "",
    val companyName: String? = null, // Optional
    val purposeOfVisit: String? = null, // Optional
    val employeeVisitedId: String = "", // ID of the employee they visited (links to Employee document)
    // Denormalized: For easier display without an extra query to the 'employees' collection
    val employeeVisitedName: String = "",
    @ServerTimestamp
    val checkInTime: Date? = null, // Server timestamp for check-in
    val checkOutTime: Date? = null, // Nullable for when visitor hasn't checked out yet
    val hasCheckedOut: Boolean = false // Flag for check-out status
)