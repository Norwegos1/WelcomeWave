package com.exposystems.welcomewave.data

import android.util.Log
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmployeeRepository @Inject constructor(
    private val employeeDao: EmployeeDao,
    private val notificationApiService: NotificationApiService
) {

    fun getEmployees(): Flow<List<Employee>> {
        return employeeDao.getAllEmployees()
    }

    suspend fun getEmployee(id: Int): Employee? {
        return employeeDao.getEmployeeById(id)
    }

    suspend fun addEmployee(employee: Employee) {
        employeeDao.insert(employee)
    }

    suspend fun deleteEmployee(employee: Employee) {
        employeeDao.delete(employee)
    }

    suspend fun sendCheckInNotification(request: CheckInRequest): Boolean {
        return try {
            val response = notificationApiService.sendCheckInNotification(request)
            if (response.isSuccessful) {
                Log.d("NotificationSuccess", "Response was successful!")
                true
            } else {
                // This will log the error response from the server
                val errorBody = response.errorBody()?.string()
                Log.e("NotificationError", "Unsuccessful response: ${response.code()} - $errorBody")
                false
            }
        } catch (e: Exception) {
            Log.e("NotificationError", "Network call failed with exception", e)
            false
        }
    }
}