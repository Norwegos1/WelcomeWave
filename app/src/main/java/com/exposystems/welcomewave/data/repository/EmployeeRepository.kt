package com.exposystems.welcomewave.data.repository

import com.exposystems.welcomewave.data.model.Employee // Ensure this imports your NEW Employee data class
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow // Import callbackFlow for real-time updates
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmployeeRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val employeesCollection = firestore.collection("employees")

    // Real-time Flow to get all employees from Firestore
    // This will emit a new list of employees every time there's a change in the 'employees' collection
    fun getAllEmployees(): Flow<List<Employee>> = callbackFlow {
        val subscription = employeesCollection
            .orderBy("firstName") // Order by first name for consistent display
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e) // Close the flow with the error if an exception occurs
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val employees = snapshot.toObjects(Employee::class.java)
                    trySend(employees).isSuccess // Send the updated list to the flow
                }
            }
        // Ensure the listener is removed when the flow is no longer collected to prevent memory leaks
        awaitClose { subscription.remove() }
    }

    // Method to get a single employee by their Firestore ID (one-time fetch)
    suspend fun getEmployeeById(id: String): Employee? {
        return try {
            val document = employeesCollection.document(id).get().await()
            document.toObject(Employee::class.java)
        } catch (e: Exception) {
            // Log the error for debugging. Use Android's Log.e instead of println in real app.
            // Log.e("EmployeeRepository", "Error getting employee by ID $id: $e")
            null
        }
    }

    // Method to add a new employee to Firestore
    suspend fun addEmployee(employee: Employee) {
        try {
            // If employee.id is an empty string, Firestore will auto-generate a unique ID.
            // Otherwise, it will use the provided ID for the document.
            employeesCollection.add(employee).await()
        } catch (e: Exception) {
            // Log the error
            // Log.e("EmployeeRepository", "Error adding employee: $e")
        }
    }

    // Method to update an existing employee in Firestore
    suspend fun updateEmployee(employee: Employee) {
        try {
            // Use the employee's ID to reference the specific document to update
            employeesCollection.document(employee.id).set(employee).await()
        } catch (e: Exception) {
            // Log the error
            // Log.e("EmployeeRepository", "Error updating employee: $e")
        }
    }

    // Method to delete an employee from Firestore
    suspend fun deleteEmployee(employeeId: String) {
        try {
            employeesCollection.document(employeeId).delete().await()
        } catch (e: Exception) {
            // Log the error
            // Log.e("EmployeeRepository", "Error deleting employee: $e")
        }
    }

    // IMPORTANT: The methods below are commented out because they are Room-specific
    // or belong to a different repository (e.g., VisitorLogRepository).
    // You will implement equivalent functionality for VisitorLog in a separate repository.

    // fun getEmployees(): Flow<List<Employee>> {
    //     return employeeDao.getAllEmployees()
    // }

    // suspend fun getEmployee(id: Int): Employee? {
    //     return employeeDao.getEmployeeById(id)
    // }

    // suspend fun sendCheckInNotification(request: CheckInRequest): Boolean { ... } // Belongs elsewhere
    // suspend fun logCheckIn(checkInRequest: CheckInRequest, employeeId: Int) { ... } // Belongs to VisitorLogRepository
    // fun getCheckedInVisitors(): Flow<List<CheckInLog>> { ... } // Belongs to VisitorLogRepository
    // suspend fun logCheckOut(log: CheckInLog) { ... } // Belongs to VisitorLogRepository
    // fun getVisitorLogs(): Flow<List<CheckInLog>> { ... } // Belongs to VisitorLogRepository
}