package com.example.munchly.domain.usecases

import com.example.munchly.data.models.RegistrationData
import com.example.munchly.data.models.User
import com.example.munchly.data.repository.AuthRepository

class RegisterUseCases(
    private val repository: AuthRepository = AuthRepository()
) {
    suspend fun registerWithEmail(registrationData: RegistrationData): Result<User> {
        return repository.registerWithEmail(registrationData)
    }
}