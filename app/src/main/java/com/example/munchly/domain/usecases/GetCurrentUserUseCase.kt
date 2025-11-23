package com.example.munchly.domain.usecases

import com.example.munchly.data.models.User
import com.example.munchly.data.repository.AuthRepository
import javax.inject.Inject

// Use case for retrieving current authenticated user
class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): User? {
        return authRepository.getCurrentUser()
    }
}