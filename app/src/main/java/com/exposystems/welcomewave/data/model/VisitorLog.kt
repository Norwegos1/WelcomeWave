package com.exposystems.welcomewave.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class VisitorLog(
    @DocumentId
    val id: String = "",
    val visitorName: String = "",
    val companyName: String? = null,
    val purposeOfVisit: String? = null,
    val employeeVisitedId: String = "",
    val employeeVisitedName: String = "",
    @ServerTimestamp
    val checkInTime: Date? = null,
    val checkOutTime: Date? = null,
    val hasCheckedOut: Boolean = false
)