package com.exposystems.welcomewave.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmployeeRepository @Inject constructor(
    private val employeeDao: EmployeeDao // Hilt needs to know how to provide this DAO
) {

    fun getEmployees(): Flow<List<Employee>> {
        return employeeDao.getAllEmployees()
    }

    suspend fun addEmployee(employee: Employee) {
        employeeDao.insert(employee)
    }

    suspend fun deleteEmployee(employee: Employee) {
        employeeDao.delete(employee)
    }

    suspend fun getEmployee(id: Int): Employee? {
        return employeeDao.getEmployeeById(id)
    }
}