package com.example.munchly.domain.usecases

import com.example.munchly.data.models.User
import com.example.munchly.data.repository.AuthRepository
import javax.inject.Inject

// Use case for user login with email and password
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return authRepository.signInWithEmail(email, password)
    }
}