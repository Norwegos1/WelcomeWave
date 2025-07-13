package com.exposystems.welcomewave.di

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing core Firebase service dependencies.
 */
@Module
@InstallIn(SingletonComponent::class) // Scopes the dependency to the application's lifecycle.
object FirebaseModule {

    /**
     * Provides a single, application-wide instance of [FirebaseFirestore].
     * This instance is the main entry point for all database operations, such as
     * accessing collections and documents.
     *
     * @return A singleton instance of FirebaseFirestore.
     */
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }
}