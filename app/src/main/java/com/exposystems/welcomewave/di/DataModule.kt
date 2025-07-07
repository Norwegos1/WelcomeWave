package com.exposystems.welcomewave.di

import com.exposystems.welcomewave.data.repository.EmployeeRepository
import com.exposystems.welcomewave.data.repository.VisitorLogRepository
import com.exposystems.welcomewave.data.NotificationApiService
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn

import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule { // Renamed from DatabaseModule to DataModule for clarity

    // Provide the Firestore-based EmployeeRepository
    @Provides
    @Singleton
    fun provideEmployeeRepository(
        firestore: FirebaseFirestore // Hilt will inject this from FirebaseModule
    ): EmployeeRepository {
        return EmployeeRepository(firestore)
    }

    // Provide the Firestore-based VisitorLogRepository
    @Provides
    @Singleton
    fun provideVisitorLogRepository(
        firestore: FirebaseFirestore, // Hilt will inject this from FirebaseModule
        notificationApiService: NotificationApiService // Hilt will inject this (ensure its own module provides it!)
    ): VisitorLogRepository {
        return VisitorLogRepository(firestore, notificationApiService)
    }
}