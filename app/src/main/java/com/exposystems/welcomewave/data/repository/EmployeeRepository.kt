package com.exposystems.welcomewave.data.repository

import android.util.Log
import com.exposystems.welcomewave.data.model.Employee
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmployeeRepository @Inject constructor(
    @Suppress("UnusedPrivateProperty")
    private val firestore: FirebaseFirestore
) {
    private val employeesCollection = firestore.collection("employees")

    /**
     * Provides a real-time Flow of all employees from the "employees" Firestore collection.
     * Emits a new list every time there's a change in the collection.
     */
    fun getAllEmployees(): Flow<List<Employee>> = callbackFlow {
        val subscription = employeesCollection
            .orderBy("firstName")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    Log.e(
                        "EmployeeRepository",
                        "Error getting real-time employees: ${e.message}",
                        e
                    )
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val employees = snapshot.toObjects(Employee::class.java)
                    trySend(employees).isSuccess
                }
            }
        awaitClose { subscription.remove() }
    }

    /**
     * Retrieves a single employee by their Firestore document ID.
     *
     * @param id The Firestore document ID of the employee.
     * @return The Employee object if found, otherwise null.
     */
    suspend fun getEmployeeById(id: String): Employee? {
        return try {
            val document = employeesCollection.document(id).get().await()
            document.toObject(Employee::class.java)
        } catch (e: Exception) {
            Log.e("EmployeeRepository", "Error getting employee by ID $id: ${e.message}", e)
            null
        }
    }

    /**
     * Adds a new employee to the "employees" Firestore collection.
     * If the employee's ID is an empty string, Firestore will auto-generate a unique ID.
     *
     * @param employee The Employee object to add.
     */
    suspend fun addEmployee(employee: Employee) {
        try {
            employeesCollection.add(employee).await()
        } catch (e: Exception) {
            Log.e("EmployeeRepository", "Error adding employee: ${e.message}", e)
        }
    }

    /**
     * Updates an existing employee in the "employees" Firestore collection.
     * Uses the employee's ID to reference the specific document to update.
     *
     * @param employee The Employee object with updated data.
     */
    suspend fun updateEmployee(employee: Employee) {
        try {
            employeesCollection.document(employee.id).set(employee).await()
        } catch (e: Exception) {
            Log.e("EmployeeRepository", "Error updating employee: ${e.message}", e)
        }
    }

    /**
     * Deletes an employee from the "employees" Firestore collection.
     *
     * @param employeeId The Firestore document ID of the employee to delete.
     */
    suspend fun deleteEmployee(employeeId: String) {
        try {
            employeesCollection.document(employeeId).delete().await()
        } catch (e: Exception) {
            Log.e("EmployeeRepository", "Error deleting employee $employeeId: ${e.message}", e)
        }
    }
}