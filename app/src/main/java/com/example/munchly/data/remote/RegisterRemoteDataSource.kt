package com.example.munchly.data.remote

import com.example.munchly.data.models.User
import com.example.munchly.data.models.UserType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface RegisterRemoteDataSource {
    suspend fun registerUser(
        email: String,
        password: String,
        username: String,
        userType: UserType
    ): User
}

class RegisterRemoteDataSourceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : RegisterRemoteDataSource {

    override suspend fun registerUser(
        email: String,
        password: String,
        username: String,
        userType: UserType
    ): User {
        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        val firebaseUser = authResult.user ?: throw Exception("User creation failed")

        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(username)
            .build()
        firebaseUser.updateProfile(profileUpdates).await()

        val user = User(
            uid = firebaseUser.uid,
            email = email,
            userType = userType,
            username = username
        )

        firestore.collection("users")
            .document(firebaseUser.uid)
            .set(user)
            .await()

        return user
    }
}