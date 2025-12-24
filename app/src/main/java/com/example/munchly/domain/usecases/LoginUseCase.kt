package com.example.munchly.domain.usecases

import com.example.munchly.data.models.User
import com.example.munchly.data.repository.LoginRepository

class LoginUseCase(
    private val loginRepository: LoginRepository
) {

    suspend operator fun invoke(
        email: String,
        password: String
    ): Result<User> {
        return loginRepository.loginUser(email, password)
    }
}
