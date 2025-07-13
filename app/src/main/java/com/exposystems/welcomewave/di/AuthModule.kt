package com.exposystems.welcomewave.di

import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing Firebase Authentication related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class) // Scopes the dependency to the application's lifecycle.
object AuthModule {

    /**
     * Provides a single, application-wide instance of [FirebaseAuth].
     * This instance is the entry point for all Firebase authentication operations,
     * such as signing in, getting the current user, etc.
     *
     * @return A singleton instance of FirebaseAuth.
     */
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
}