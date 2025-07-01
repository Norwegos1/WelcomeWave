package com.exposystems.welcomewave.data

data class CheckInRequest(
    val employeeEmail: String,
    val visitorCompany: String,
    val visitorNames: List<String>
)