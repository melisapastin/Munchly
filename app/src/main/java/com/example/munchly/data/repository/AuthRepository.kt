package com.example.munchly.data.repository

import android.util.Log
import com.example.munchly.data.models.RegistrationData
import com.example.munchly.data.models.User
import com.example.munchly.data.models.UserType
import com.example.munchly.data.remote.UserRemoteDataSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRemoteDataSource: UserRemoteDataSource
) {

    private companion object {
        const val TAG = "AuthRepository"
    }

    // Authenticate existing user with email/password
    suspend fun signInWithEmail(email: String, password: String): Result<User> {
        return try {
            Log.d(TAG, "Attempting sign in with email: $email")
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: return Result.failure(Exception("Authentication failed - no user returned"))

            Log.d(TAG, "Firebase auth successful, UID: ${firebaseUser.uid}")

            // Get user data from Firestore
            val user = userRemoteDataSource.getUser(firebaseUser.uid)
            if (user == null) {
                Log.e(TAG, "User data not found in Firestore for UID: ${firebaseUser.uid}")
                return Result.failure(Exception("User profile not found. Please try registering again."))
            }

            Log.d(TAG, "User data retrieved successfully: ${user.email}")
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Sign in failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Register new user and create their profile
    suspend fun registerWithEmail(registrationData: RegistrationData): Result<User> {
        return try {
            Log.d(TAG, "Attempting registration with email: ${registrationData.email}")

            // 1. Create Firebase Auth account
            val authResult = auth.createUserWithEmailAndPassword(
                registrationData.email,
                registrationData.password
            ).await()

            val firebaseUser = authResult.user ?: return Result.failure(Exception("Registration failed - no user returned"))

            Log.d(TAG, "Firebase auth user created, UID: ${firebaseUser.uid}")

            // 2. Set display name
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(registrationData.username)
                .build()
            firebaseUser.updateProfile(profileUpdates).await()

            // 3. Create user document in Firestore
            val user = User(
                uid = firebaseUser.uid,
                email = registrationData.email,
                userType = registrationData.userType,
                username = registrationData.username,
                createdAt = Date()
            )

            Log.d(TAG, "Creating user document in Firestore with UID: ${user.uid}")
            val success = userRemoteDataSource.createUser(user)
            if (!success) {
                Log.e(TAG, "Failed to create user document in Firestore")
                // Optional: Delete the Firebase auth user if Firestore fails
                firebaseUser.delete().await()
                return Result.failure(Exception("Failed to create user profile in database"))
            }

            Log.d(TAG, "User registration completed successfully")
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Registration failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Get current authenticated user
    fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null
        return User(
            uid = firebaseUser.uid,
            email = firebaseUser.email ?: "",
            userType = UserType.FOOD_LOVER, // Default, will be loaded from Firestore when needed
            username = firebaseUser.displayName
        )
    }

    // Sign out current user
    fun signOut() {
        auth.signOut()
    }
}