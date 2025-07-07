package com.exposystems.welcomewave.data.repository

import android.util.Log
import com.exposystems.welcomewave.data.CheckInRequest // Assuming this is still your data class for the API call
import com.exposystems.welcomewave.data.NotificationApiService // Keep this dependency
import com.exposystems.welcomewave.data.model.VisitorLog // Import your NEW VisitorLog data class
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date // For Timestamp conversion if needed, though ServerTimestamp handles it

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VisitorLogRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val notificationApiService: NotificationApiService // Inject NotificationApiService here
) {
    // Reference to the "visitorLogs" collection in Firestore
    private val visitorLogsCollection = firestore.collection("visitorLogs")

    /**
     * Provides a real-time Flow of all visitor logs, ordered by check-in time.
     */
    fun getAllVisitorLogs(): Flow<List<VisitorLog>> = callbackFlow {
        val subscription = visitorLogsCollection
            .orderBy("checkInTime", Query.Direction.DESCENDING) // Order by latest check-in first
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e) // Close the flow with the error if an exception occurs
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val logs = snapshot.toObjects(VisitorLog::class.java)
                    trySend(logs).isSuccess // Send the updated list to the flow
                }
            }
        // Ensure the listener is removed when the flow is no longer collected
        awaitClose { subscription.remove() }
    }

    /**
     * Provides a real-time Flow of visitors who are currently checked in (haven't checked out yet).
     */
    fun getCurrentlyCheckedInVisitors(): Flow<List<VisitorLog>> = callbackFlow {
        val subscription = visitorLogsCollection
            .whereEqualTo("hasCheckedOut", false) // Filter for not checked out
            .orderBy("checkInTime", Query.Direction.ASCENDING) // Order by oldest check-in first
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val currentVisitors = snapshot.toObjects(VisitorLog::class.java)
                    trySend(currentVisitors).isSuccess
                }
            }
        awaitClose { subscription.remove() }
    }

    /**
     * Logs a new visitor check-in in Firestore and sends a notification.
     *
     * @param checkInRequest The request containing visitor details.
     * @param employeeFirestoreId The Firestore ID of the employee being visited.
     * @param employeeName The name of the employee being visited (for denormalization).
     */
    suspend fun logCheckIn(
        checkInRequest: CheckInRequest,
        employeeFirestoreId: String,
        employeeName: String
    ): Boolean {
        return try {
            val visitorLog = VisitorLog(
                visitorName = checkInRequest.visitorNames.joinToString(", "), // Join names if multiple
                companyName = checkInRequest.visitorCompany,
                employeeVisitedId = employeeFirestoreId,
                employeeVisitedName = employeeName,
                // checkInTime will be set by @ServerTimestamp
                hasCheckedOut = false
            )
            visitorLogsCollection.add(visitorLog).await()

            // Send notification after successful log
            sendCheckInNotification(checkInRequest) // Use the existing notification service
            true
        } catch (e: Exception) {
            Log.e("VisitorLogRepository", "Error logging check-in: ${e.message}", e)
            false
        }
    }

    /**
     * Logs a visitor check-out by updating an existing visitor log document.
     *
     * @param visitorLogId The Firestore ID of the visitor log to update.
     */
    suspend fun logCheckOut(visitorLogId: String): Boolean {
        return try {
            // Update only specific fields (checkOutTime and hasCheckedOut)
            visitorLogsCollection.document(visitorLogId).update(
                mapOf(
                    "checkOutTime" to Date(), // Set current date as check-out time
                    "hasCheckedOut" to true
                )
            ).await()
            true
        } catch (e: Exception) {
            Log.e("VisitorLogRepository", "Error logging check-out for $visitorLogId: ${e.message}", e)
            false
        }
    }

    // You might also need a way to get a specific visitor log by ID if the UI requires it for detailed view or check-out by ID
    suspend fun getVisitorLogById(logId: String): VisitorLog? {
        return try {
            visitorLogsCollection.document(logId).get().await().toObject(VisitorLog::class.java)
        } catch (e: Exception) {
            Log.e("VisitorLogRepository", "Error getting visitor log by ID $logId: ${e.message}", e)
            null
        }
    }

    // This private function wraps your existing notification service call
    private suspend fun sendCheckInNotification(request: CheckInRequest): Boolean {
        return try {
            val response = notificationApiService.sendCheckInNotification(request)
            if (response.isSuccessful) {
                true
            } else {
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