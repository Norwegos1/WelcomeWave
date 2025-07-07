package com.exposystems.welcomewave.di

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        // This line gets the default instance of Firebase Firestore
        // It automatically uses the configuration from google-services.json
        return FirebaseFirestore.getInstance()
    }
}