package com.exposystems.welcomewave.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmployeeRepository @Inject constructor(
    private val employeeDao: EmployeeDao,
    private val checkInLogDao: CheckInLogDao, // This should already be here
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

    suspend fun updateEmployee(employee: Employee) {
        employeeDao.update(employee)
    }

    suspend fun deleteEmployee(employee: Employee) {
        employeeDao.delete(employee)
    }

    suspend fun sendCheckInNotification(request: CheckInRequest): Boolean {
        return try {
            val response = notificationApiService.sendCheckInNotification(request)
            if (response.isSuccessful) {
                true
            } else {
                val errorBody = response.errorBody()?.string()
                android.util.Log.e("NotificationError", "Unsuccessful response: ${response.code()} - $errorBody")
                false
            }
        } catch (e: Exception) {
            android.util.Log.e("NotificationError", "Network call failed with exception", e)
            false
        }
    }

    suspend fun logCheckIn(checkInRequest: CheckInRequest, employeeId: Int) {
        val log = CheckInLog(
            visitorCompany = checkInRequest.visitorCompany,
            visitorNames = checkInRequest.visitorNames.joinToString(", "),
            employeeId = employeeId
        )
        checkInLogDao.insert(log)
    }

    // This is one of the missing functions
    fun getCheckedInVisitors(): Flow<List<CheckInLog>> {
        return checkInLogDao.getCurrentlyCheckedIn()
    }

    // This is the other missing function
    suspend fun logCheckOut(log: CheckInLog) {
        checkInLogDao.setCheckOutTime(log.id, System.currentTimeMillis())
    }

    fun getVisitorLogs(): Flow<List<CheckInLog>> {
        return checkInLogDao.getAllLogs()
    }
}