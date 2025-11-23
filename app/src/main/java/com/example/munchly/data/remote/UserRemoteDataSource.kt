package com.example.munchly.data.remote

import android.util.Log
import com.example.munchly.data.models.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private companion object {
        const val TAG = "UserRemoteDataSource"
    }

    suspend fun getUser(userId: String): User? = try {
        Log.d(TAG, "Fetching user with ID: $userId")
        val document = firestore.collection("users").document(userId).get().await()

        if (document.exists()) {
            val user = document.toObject(User::class.java)
            Log.d(TAG, "User found: ${user?.email}")
            user
        } else {
            Log.w(TAG, "User document does not exist for ID: $userId")
            null
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching user $userId: ${e.message}", e)
        null
    }

    suspend fun createUser(user: User): Boolean = try {
        Log.d(TAG, "Creating user with ID: ${user.uid}")
        firestore.collection("users").document(user.uid).set(user).await()
        Log.d(TAG, "User created successfully")
        true
    } catch (e: Exception) {
        Log.e(TAG, "Error creating user: ${e.message}", e)
        false
    }
}