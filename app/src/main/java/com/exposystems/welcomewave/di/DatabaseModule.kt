package com.exposystems.welcomewave.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.exposystems.welcomewave.data.AppDatabase
import com.exposystems.welcomewave.data.Employee
import com.exposystems.welcomewave.data.EmployeeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Provider
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext appContext: Context,
        // We use a Provider to prevent a circular dependency
        employeeDaoProvider: Provider<EmployeeDao>
    ): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "welcome_wave_database"
        )
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // This runs only once when the database is first created.
                    // We're using the provider to get the DAO instance here.
                    CoroutineScope(Dispatchers.IO).launch {
                        val employeeDao = employeeDaoProvider.get()
                        employeeDao.insert(Employee(name = "Samantha Jones", email = "samantha.j@exposystems.com", title = "Sales Director"))
                        employeeDao.insert(Employee(name = "David Chen", email = "david.c@exposystems.com", title = "Lead Engineer"))
                        employeeDao.insert(Employee(name = "Maria Garcia", email = "maria.g@exposystems.com", title = "Project Manager"))
                    }
                }
            })
            .build()
    }

    @Provides
    fun provideEmployeeDao(database: AppDatabase): EmployeeDao {
        return database.employeeDao()
    }
}