package com.example.munchly.domain.usecases

import com.example.munchly.domain.models.UserDomain
import com.example.munchly.domain.repository.AuthRepository

class GoogleLoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(idToken: String): Result<UserDomain> {
        return repository.signInWithGoogle(idToken)
    }
}