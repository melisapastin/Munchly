package com.example.munchly.data.repository

import com.example.munchly.data.fire.FirebaseDataSource
import com.example.munchly.data.models.User

class UserRepository(private val firebase: FirebaseDataSource) {

    suspend fun getUser(id: String): User? {
        return firebase.getUserById(id)
    }

    suspend fun addUser(user: User) {
        firebase.createUser(user)
    }
}
