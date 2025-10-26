package com.example.munchly.data.fire

import com.example.munchly.data.models.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


class FirebaseDataSource(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun getUserById(userId: String): User? {
        val snapshot = firestore.collection("users").document(userId).get().await()
        return snapshot.toObject(User::class.java)
    }

    suspend fun createUser(user: User) {
        firestore.collection("users").document(user.id).set(user).await()
    }
}