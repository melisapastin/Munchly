package com.example.munchly.domain.usecases

import com.example.munchly.data.models.User
import com.example.munchly.data.repository.AuthRepository

class AuthUseCases(
    private val repository: AuthRepository = AuthRepository()
) {
    suspend fun loginWithEmail(email: String, password: String): Result<User> {
        return repository.signInWithEmail(email, password)
    }

    suspend fun loginWithGoogle(idToken: String): Result<User> {
        return repository.signInWithGoogle(idToken)
    }
}