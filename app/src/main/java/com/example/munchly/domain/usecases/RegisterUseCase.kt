package com.example.munchly.domain.usecases

import com.example.munchly.data.models.User
import com.example.munchly.data.models.UserType
import com.example.munchly.data.repository.RegisterRepository

data class RegisterValidationResult(
    val usernameError: String?,
    val emailError: String?,
    val passwordError: String?,
    val isValid: Boolean
)

class RegisterUseCase(
    private val registerRepository: RegisterRepository
) {

    fun validateCredentials(
        username: String,
        email: String,
        password: String
    ): RegisterValidationResult {
        val usernameError = when {
            username.isBlank() -> "Username cannot be empty"
            username.length < 3 -> "Username must be at least 3 characters"
            username.length > 20 -> "Username must be less than 20 characters"
            !username.matches(Regex("^[a-zA-Z0-9_]+$")) ->
                "Username can only contain letters, numbers and underscores"
            else -> null
        }

        val emailError = when {
            email.isBlank() -> "Email cannot be empty"
            !isValidEmail(email) -> "Please enter a valid email"
            else -> null
        }

        val passwordError = when {
            password.isBlank() -> "Password cannot be empty"
            password.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }

        return RegisterValidationResult(
            usernameError = usernameError,
            emailError = emailError,
            passwordError = passwordError,
            isValid = usernameError == null && emailError == null && passwordError == null
        )
    }

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

    private fun isValidEmail(email: String): Boolean {
        return email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
    }
}