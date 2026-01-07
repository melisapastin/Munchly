package com.example.munchly.domain.usecases

import com.example.munchly.domain.models.LoginInput
import com.example.munchly.domain.models.RegisterInput
import com.example.munchly.domain.models.UserDomain
import com.example.munchly.domain.repository.AuthRepository
import com.example.munchly.domain.services.AuthValidator
import com.example.munchly.domain.services.InputNormalizer

// ============================================================================
// USE CASES - Application-specific authentication operations
// ============================================================================

/**
 * Authenticates a user with email and password.
 *
 * Responsibilities:
 * - Normalizes user input (lowercase email, trim whitespace)
 * - Validates input according to business rules
 * - Delegates to repository for actual authentication
 *
 * Returns Result<UserDomain> where:
 * - Success contains authenticated user data
 * - Failure contains domain exception describing what went wrong
 */
class LoginUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(input: LoginInput): Result<UserDomain> {
        // Normalize input to ensure consistent format
        val normalizedEmail = InputNormalizer.normalizeEmail(input.email)

        // Validate normalized input and delegate to repository
        return try {
            AuthValidator.validateLogin(normalizedEmail, input.password)
            repository.login(normalizedEmail, input.password)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * Creates a new user account.
 *
 * Responsibilities:
 * - Normalizes user input (lowercase email, trim whitespace)
 * - Validates all registration fields according to business rules
 * - Delegates to repository for account creation
 *
 * Returns Result<UserDomain> where:
 * - Success contains newly created user data
 * - Failure contains domain exception (validation, duplicate email/username, etc.)
 */
class RegisterUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(input: RegisterInput): Result<UserDomain> {
        // Normalize input to ensure consistent format
        val normalizedEmail = InputNormalizer.normalizeEmail(input.email)
        val normalizedUsername = InputNormalizer.normalizeUsername(input.username)

        // Validate all fields and delegate to repository
        return try {
            AuthValidator.validateRegistration(
                email = normalizedEmail,
                password = input.password,
                username = normalizedUsername
            )

            repository.register(
                email = normalizedEmail,
                password = input.password,
                username = normalizedUsername,
                userType = input.userType
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}