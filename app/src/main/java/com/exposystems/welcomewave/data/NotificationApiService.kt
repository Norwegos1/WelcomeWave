package com.exposystems.welcomewave.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface NotificationApiService {
    @POST("YOUR_FUNCTION_URL_HERE") // We will replace this later
    suspend fun sendCheckInNotification(
        @Body request: CheckInRequest
    ): Response<Unit> // We just care if it was successful (200 OK)
}