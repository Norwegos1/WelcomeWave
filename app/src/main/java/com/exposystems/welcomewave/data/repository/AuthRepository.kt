package com.exposystems.welcomewave.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser


    /**
     * Logs in an existing user with email and password.
     * @return The logged-in FirebaseUser or null on failure.
     */
    suspend fun loginUser(email: String, password: String): FirebaseUser? {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            result.user
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error logging in user: ${e.message}", e)
            null
        }
    }

    /**
     * Logs out the current user.
     * Returns true on successful logout, false on error.
     */
    // CHANGED: From fun logoutUser() to suspend fun signOut(): Boolean
    fun signOut(): Boolean {
        return try {
            firebaseAuth.signOut()
            true // Logout successful
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error logging out user: ${e.message}", e)
            false // Logout failed
        }
    }

}