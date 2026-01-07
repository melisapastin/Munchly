package com.example.munchly.domain.services

import com.example.munchly.domain.exceptions.DomainException

/**
 * Service responsible for validating authentication data according to business rules.
 * Pure functions with no side effects - no Android dependencies.
 *
 * All validation methods throw DomainException.ValidationError on failure,
 * providing a consistent error handling pattern across the domain layer.
 */
object AuthValidator {

    // Compiled regex patterns for efficient validation
    private val EMAIL_REGEX = Regex("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")
    private val USERNAME_REGEX = Regex("^[a-zA-Z0-9_]+$")

    /**
     * Validates login input.
     * @throws DomainException.ValidationError if validation fails
     */
    fun validateLogin(email: String, password: String) {
        validateEmail(email)
        validatePassword(password)
    }

    /**
     * Validates all registration fields.
     * @throws DomainException.ValidationError if any validation fails
     */
    fun validateRegistration(
        email: String,
        password: String,
        username: String
    ) {
        validateEmail(email)
        validatePassword(password)
        validateUsername(username)
    }

    /**
     * Validates email format and requirements.
     * Assumes email is already trimmed and normalized.
     *
     * @throws DomainException.ValidationError if validation fails
     */
    fun validateEmail(email: String) {
        when {
            email.isBlank() ->
                throw DomainException.ValidationError("Email is required")
            !isValidEmailFormat(email) ->
                throw DomainException.ValidationError("Invalid email format")
        }
    }

    /**
     * Validates password requirements.
     *
     * @throws DomainException.ValidationError if validation fails
     */
    fun validatePassword(password: String) {
        when {
            password.isBlank() ->
                throw DomainException.ValidationError("Password is required")
            password.length < AuthValidationLimits.MIN_PASSWORD_LENGTH ->
                throw DomainException.ValidationError(
                    "Password must be at least ${AuthValidationLimits.MIN_PASSWORD_LENGTH} characters"
                )
        }
    }

    /**
     * Validates username format and length.
     * Assumes username is already trimmed.
     *
     * @throws DomainException.ValidationError if validation fails
     */
    fun validateUsername(username: String) {
        when {
            username.isBlank() ->
                throw DomainException.ValidationError("Username is required")
            username.length < AuthValidationLimits.MIN_USERNAME_LENGTH ->
                throw DomainException.ValidationError(
                    "Username must be at least ${AuthValidationLimits.MIN_USERNAME_LENGTH} characters"
                )
            username.length > AuthValidationLimits.MAX_USERNAME_LENGTH ->
                throw DomainException.ValidationError(
                    "Username must be less than ${AuthValidationLimits.MAX_USERNAME_LENGTH} characters"
                )
            !isValidUsernameFormat(username) ->
                throw DomainException.ValidationError(
                    "Username can only contain letters, numbers, and underscores"
                )
        }
    }

    /**
     * Validates email format using regex.
     * Pure Kotlin implementation - no Android dependencies.
     */
    private fun isValidEmailFormat(email: String): Boolean {
        return EMAIL_REGEX.matches(email)
    }

    /**
     * Validates username format.
     * Allows letters, numbers, and underscores only.
     */
    private fun isValidUsernameFormat(username: String): Boolean {
        return USERNAME_REGEX.matches(username)
    }
}

/**
 * Validation limits for authentication data.
 * These constants are used both for validation and for UI displays.
 */
object AuthValidationLimits {
    const val MIN_PASSWORD_LENGTH = 6
    const val MIN_USERNAME_LENGTH = 3
    const val MAX_USERNAME_LENGTH = 20
}

/**
 * Service for normalizing user input before validation.
 * Ensures consistent data format across the application.
 */
object InputNormalizer {

    /**
     * Normalizes email: trim whitespace and convert to lowercase.
     * Email addresses are case-insensitive per RFC 5321.
     */
    fun normalizeEmail(email: String): String {
        return email.trim().lowercase()
    }

    /**
     * Normalizes username: trim whitespace only.
     * Username case is preserved as entered by user.
     */
    fun normalizeUsername(username: String): String {
        return username.trim()
    }
}