package com.example.munchly.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.munchly.domain.exceptions.*
import com.example.munchly.domain.models.RegisterInput
import com.example.munchly.domain.models.UserDomain
import com.example.munchly.domain.models.UserTypeDomain
import com.example.munchly.domain.services.InputNormalizer
import com.example.munchly.domain.usecases.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ============================================================================
// STATE
// ============================================================================

/**
 * UI state for registration credentials screen.
 * Represents all data needed to render the registration form.
 */
data class RegisterCredentialsState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val registrationSuccess: Boolean = false,
    val user: UserDomain? = null
)

// ============================================================================
// VIEWMODEL
// ============================================================================

/**
 * ViewModel for registration credentials screen.
 * Manages UI state and coordinates registration flow with the domain layer.
 *
 * Responsibilities:
 * - Manage UI state (input fields, loading, errors)
 * - Normalize user input as they type
 * - Delegate validation and account creation to use case
 * - Map domain exceptions to user-friendly messages
 */
class RegisterCredentialsViewModel(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterCredentialsState())
    val uiState: StateFlow<RegisterCredentialsState> = _uiState.asStateFlow()

    // ========================================================================
    // INPUT HANDLERS
    // ========================================================================

    /**
     * Handles username input changes.
     * Normalizes username immediately (trim) as user types.
     */
    fun onUsernameChange(username: String) {
        val normalizedUsername = InputNormalizer.normalizeUsername(username)

        _uiState.update {
            it.copy(
                username = normalizedUsername,
                error = null
            )
        }
    }

    /**
     * Handles email input changes.
     * Normalizes email immediately (lowercase, trim) as user types.
     */
    fun onEmailChange(email: String) {
        val normalizedEmail = InputNormalizer.normalizeEmail(email)

        _uiState.update {
            it.copy(
                email = normalizedEmail,
                error = null
            )
        }
    }

    /**
     * Handles password input changes.
     */
    fun onPasswordChange(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                error = null
            )
        }
    }

    // ========================================================================
    // REGISTRATION OPERATION
    // ========================================================================

    /**
     * Attempts to create a new user account.
     * Use case handles validation - ViewModel only manages UI state.
     *
     * @param userType The type of account to create (Food Lover or Restaurant Owner)
     */
    fun register(userType: UserTypeDomain) {
        val currentState = _uiState.value

        // Show loading state
        _uiState.update {
            it.copy(
                isLoading = true,
                error = null
            )
        }

        viewModelScope.launch {
            // Email and username already normalized from onChange handlers
            val input = RegisterInput(
                email = currentState.email,
                password = currentState.password,
                username = currentState.username,
                userType = userType
            )

            // Use case handles validation and account creation
            val result = registerUseCase(input)

            // Hide loading state
            _uiState.update { it.copy(isLoading = false) }

            // Update UI based on result
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        registrationSuccess = true,
                        user = result.getOrNull()
                    )
                }
            } else {
                val errorMessage = mapErrorToMessage(result.exceptionOrNull())
                _uiState.update { it.copy(error = errorMessage) }
            }
        }
    }

    // ========================================================================
    // ERROR MAPPING
    // ========================================================================

    /**
     * Maps domain exceptions to user-friendly error messages.
     * This is the single source of truth for error messages in the UI layer.
     *
     * In production, these hardcoded strings should be replaced with
     * string resources (R.string.*) to support localization.
     */
    private fun mapErrorToMessage(exception: Throwable?): String {
        return when (exception) {
            is UserNotFoundException ->
                "No account found with this email"
            is InvalidCredentialsException ->
                "Invalid email or password"
            is EmailAlreadyInUseException ->
                "This email is already registered"
            is UsernameCollisionException ->
                "This username is already taken"
            is WeakPasswordException ->
                "Password is too weak. Please use a stronger password"
            is UserDataNotFoundException ->
                "Account data not found. Please contact support"
            is InvalidUserDataException ->
                exception.details
            is DomainException.NetworkError ->
                "Network error. Please check your connection"
            is DomainException.ValidationError ->
                exception.reason
            is DomainException.PermissionDenied ->
                "You don't have permission to perform this action"
            is DomainException.ResourceNotFound ->
                "Requested information not found"
            is DomainException.InvalidData ->
                exception.reason
            is DomainException.OperationFailed ->
                "Operation failed. Please try again"
            else ->
                "An unexpected error occurred. Please try again"
        }
    }
}