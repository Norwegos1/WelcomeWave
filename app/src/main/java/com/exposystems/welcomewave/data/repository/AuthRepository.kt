package com.exposystems.welcomewave.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    /**
     * Exposes the current FirebaseUser as a Flow for reactive updates.
     * Emits the current user whenever the authentication state changes (login, logout).
     */
    val currentUserFlow: Flow<FirebaseUser?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        firebaseAuth.addAuthStateListener(authStateListener)
        awaitClose {
            firebaseAuth.removeAuthStateListener(authStateListener)
        }
    }

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
    fun signOut(): Boolean {
        return try {
            firebaseAuth.signOut()
            true // Logout successful
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error logging out user: ${e.message}", e)
            false // Logout failed
        }
    }

    /**
     * Sends a password reset email to the given email address.
     * @return True if successful, false otherwise.
     */
    suspend fun sendPasswordResetEmail(email: String): Boolean {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            true
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error sending password reset email: ${e.message}", e)
            false
        }
    }
}