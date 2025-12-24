package com.example.munchly.data.remote

import com.example.munchly.data.models.User
import com.example.munchly.data.models.UserType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

interface RegisterRemoteDataSource {

    suspend fun registerUser(
        email: String,
        password: String,
        username: String,
        userType: UserType
    ): User
}

class RegisterRemoteDataSourceImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : RegisterRemoteDataSource {

    override suspend fun registerUser(
        email: String,
        password: String,
        username: String,
        userType: UserType
    ): User {
        // 1. Create user in Firebase Auth
        val authResult = auth
            .createUserWithEmailAndPassword(email, password)
            .await()

        val firebaseUser =
            authResult.user ?: throw Exception("User creation failed")

        // 2. Create user document
        val user = User(
            uid = firebaseUser.uid,
            email = email,
            userType = userType,
            username = username
        )

        // 3. Save to Firestore
        firestore.collection("users")
            .document(firebaseUser.uid)
            .set(user)
            .await()

        return user
    }
}
