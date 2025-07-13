package com.exposystems.welcomewave.di

import com.exposystems.welcomewave.data.NotificationApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Hilt module for providing networking-related dependencies, specifically the
 * Retrofit client used for communicating with the backend Cloud Function.
 */
@Module
@InstallIn(SingletonComponent::class) // Scopes the dependency to the application's lifecycle.
object NetworkModule {

    /**
     * Builds and provides a single, application-wide instance of [NotificationApiService].
     * This service is used by repositories to make the actual HTTP call to the
     * Cloud Function that sends the email notification.
     *
     * @return A singleton instance of NotificationApiService.
     */
    @Provides
    @Singleton
    fun provideNotificationApiService(): NotificationApiService {
        // The base URL of your deployed 'sendCheckInNotification' Cloud Function.
        val baseUrl = "https://sendcheckinnotification-tmklzaes2q-uc.a.run.app/"

        // Use the Retrofit builder to create the network service.
        return Retrofit.Builder()
            .baseUrl(baseUrl) // Set the base URL for all requests made by this service.
            .addConverterFactory(GsonConverterFactory.create()) // Add a converter to handle JSON data.
            .build()
            .create(NotificationApiService::class.java) // Create an implementation of our API interface.
    }
}