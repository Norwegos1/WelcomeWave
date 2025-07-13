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

/**
 * Hilt module responsible for providing data-layer dependencies, such as repositories,
 * to the rest of the application. This allows other parts of the app (like ViewModels)
 * to get instances of these classes without needing to create them directly.
 */
@Module
@InstallIn(SingletonComponent::class) // Scopes the dependencies to the application's lifecycle.
object DataModule {

    /**
     * Provides a single, application-wide instance of [EmployeeRepository].
     * @param firestore The Firestore database instance, provided by another Hilt module.
     * @return A singleton instance of EmployeeRepository for managing employee data.
     */
    @Provides
    @Singleton
    fun provideEmployeeRepository(
        firestore: FirebaseFirestore
    ): EmployeeRepository {
        return EmployeeRepository(firestore)
    }

    /**
     * Provides a single, application-wide instance of [VisitorLogRepository].
     * @param firestore The Firestore database instance.
     * @param notificationApiService The service responsible for making network calls to our email notification function.
     * @return A singleton instance of VisitorLogRepository for logging visits and triggering notifications.
     */
    @Provides
    @Singleton
    fun provideVisitorLogRepository(
        firestore: FirebaseFirestore,
        notificationApiService: NotificationApiService
    ): VisitorLogRepository {
        return VisitorLogRepository(firestore, notificationApiService)
    }
}