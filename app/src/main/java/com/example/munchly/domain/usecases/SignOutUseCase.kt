package com.example.munchly.domain.usecases

import com.example.munchly.data.repository.AuthRepository
import javax.inject.Inject

// Use case for user sign out
class SignOutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() {
        authRepository.signOut()
    }
}