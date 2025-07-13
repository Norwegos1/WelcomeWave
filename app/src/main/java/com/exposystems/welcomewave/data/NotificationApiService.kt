package com.exposystems.welcomewave.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface NotificationApiService {
    @POST(".")
    suspend fun sendCheckInNotification(
        @Body request: CheckInRequest
    ): Response<Unit>
}
