package com.exposystems.welcomewave.data

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
            // This line now uses the injected service
            val response = notificationApiService.sendCheckInNotification(request)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace() // Log the error in a real app
            false
        }
    }
}