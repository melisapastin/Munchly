package com.example.munchly.domain.usecases

import com.example.munchly.data.models.RegistrationData
import com.example.munchly.data.models.User
import com.example.munchly.data.repository.AuthRepository
import javax.inject.Inject

// Use case for user registration
class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(registrationData: RegistrationData): Result<User> {
        return authRepository.registerWithEmail(registrationData)
    }
}