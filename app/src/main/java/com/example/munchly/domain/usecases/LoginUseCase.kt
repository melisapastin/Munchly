package com.example.munchly.domain.usecases

import com.example.munchly.data.models.User
import com.example.munchly.data.repository.LoginRepository

data class LoginValidationResult(
    val emailError: String?,
    val passwordError: String?,
    val isValid: Boolean
)

class LoginUseCase(
    private val loginRepository: LoginRepository
) {

    fun validateCredentials(email: String, password: String): LoginValidationResult {
        val emailError = when {
            email.isBlank() -> "Please enter your email address"
            !isValidEmail(email) -> "Please enter a valid email address"
            else -> null
        }

        val passwordError = when {
            password.isBlank() -> "Please enter your password"
            password.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }

        return LoginValidationResult(
            emailError = emailError,
            passwordError = passwordError,
            isValid = emailError == null && passwordError == null
        )
    }

    suspend operator fun invoke(
        email: String,
        password: String
    ): Result<User> {
        return loginRepository.loginUser(email, password)
    }

    private fun isValidEmail(email: String): Boolean {
        return email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
    }
}