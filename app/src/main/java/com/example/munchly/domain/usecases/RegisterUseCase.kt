package com.example.munchly.domain.usecases

import com.example.munchly.data.models.User
import com.example.munchly.data.models.UserType
import com.example.munchly.data.repository.RegisterRepository

class RegisterUseCase(
    private val registerRepository: RegisterRepository
) {

    suspend operator fun invoke(
        email: String,
        password: String,
        username: String,
        userType: UserType
    ): Result<User> {
        return registerRepository.registerUser(
            email,
            password,
            username,
            userType
        )
    }
}
