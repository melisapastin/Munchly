package com.example.munchly.domain.managers

import com.example.munchly.data.models.User
import com.example.munchly.data.repository.UserRepository

class UserManager(private val repo: UserRepository) {

    suspend fun getFormattedUser(id: String): User? {
        val user = repo.getUser(id)
        // Example of processing
        return user?.copy(name = user.name.uppercase())
    }
}
