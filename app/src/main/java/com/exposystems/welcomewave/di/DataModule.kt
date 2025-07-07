package com.exposystems.welcomewave.di

// Make sure these imports are correct based on your project structure
import com.exposystems.welcomewave.data.repository.EmployeeRepository
import com.exposystems.welcomewave.data.repository.VisitorLogRepository // NEW: Import your VisitorLogRepository
import com.exposystems.welcomewave.data.NotificationApiService // NEW: Import NotificationApiService, as VisitorLogRepository needs it
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
// import dagger.hilt.android.qualifiers.ApplicationContext // No longer needed if Room context isn't used
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

    // IMPORTANT: Temporarily commented out (or completely remove once migration is complete)
    // the Room-related providers.
    /*
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "welcome_wave_db"
        ).build()
    }

    @Provides
    fun provideEmployeeDao(database: AppDatabase): EmployeeDao {
        return database.employeeDao()
    }

    @Provides
    fun provideCheckInLogDao(database: AppDatabase): CheckInLogDao {
        return database.checkInLogDao()
    }
    */
}