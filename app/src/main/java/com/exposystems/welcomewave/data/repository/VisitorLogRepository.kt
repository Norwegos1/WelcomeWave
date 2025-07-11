package com.exposystems.welcomewave.data.repository

import android.util.Log
import com.exposystems.welcomewave.data.CheckInRequest
import com.exposystems.welcomewave.data.NotificationApiService
import com.exposystems.welcomewave.data.model.VisitorLog
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VisitorLogRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    @Suppress("UnusedPrivateProperty")
    private val notificationApiService: NotificationApiService
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
                visitorName = checkInRequest.visitorNames.joinToString(", "),
                companyName = checkInRequest.visitorCompany,
                employeeVisitedId = employeeFirestoreId,
                employeeVisitedName = employeeName,
                hasCheckedOut = false
            )
            visitorLogsCollection.add(visitorLog).await()

            sendCheckInNotification(checkInRequest)
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
            visitorLogsCollection.document(visitorLogId).update(
                mapOf(
                    "checkOutTime" to Date(),
                    "hasCheckedOut" to true
                )
            ).await()
            true
        } catch (e: Exception) {
            Log.e("VisitorLogRepository", "Error logging check-out for $visitorLogId: ${e.message}", e)
            false
        }
    }

    // Suppress "unused" warning for this function as it's a utility that might be used later
    @Suppress("unused")
    suspend fun getVisitorLogById(logId: String): VisitorLog? {
        return try {
            visitorLogsCollection.document(logId).get().await().toObject(VisitorLog::class.java)
        } catch (e: Exception) {
            Log.e("VisitorLogRepository", "Error getting visitor log by ID $logId: ${e.message}", e)
            null
        }
    }

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