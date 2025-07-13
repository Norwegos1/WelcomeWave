package com.exposystems.welcomewave.ui.preregistered

data class PreRegisteredGuest(
    val id: String = "",
    val visitorName: String = "",
    val visitorCompany: String? = null,
    val employeeToSeeId: String = "",
    val employeeToSeeName: String = ""
)