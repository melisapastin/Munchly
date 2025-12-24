package com.example.munchly.data.remote

import com.example.munchly.data.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

interface LoginRemoteDataSource {
    suspend fun loginUser(email: String, password: String): User
}

class LoginRemoteDataSourceImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : LoginRemoteDataSource {

    override suspend fun loginUser(email: String, password: String): User {
        // 1. Authenticate with Firebase Auth
        val authResult = auth.signInWithEmailAndPassword(email, password).await()
        val firebaseUser = authResult.user ?: throw Exception("Authentication failed")

        // 2. Fetch user data from Firestore
        val userDocument = firestore.collection("users")
            .document(firebaseUser.uid)
            .get()
            .await()

        // 3. Convert to User model and validate
        val user = userDocument.toObject(User::class.java) ?: throw Exception("User data not found")

        if (user.uid.isBlank() || user.email.isBlank()) {
            throw Exception("Invalid user data in database")
        }
        return user
    }
}