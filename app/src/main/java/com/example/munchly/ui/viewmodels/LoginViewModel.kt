package com.example.munchly.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.munchly.domain.exceptions.*
import com.example.munchly.domain.models.LoginInput
import com.example.munchly.domain.models.UserDomain
import com.example.munchly.domain.services.InputNormalizer
import com.example.munchly.domain.usecases.GoogleLoginUseCase
import com.example.munchly.domain.usecases.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ============================================================================
// STATE
// ============================================================================

/**
 * UI state for login screen.
 * Represents all data needed to render the login UI.
 */
data class LoginState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val loginSuccess: Boolean = false,
    val user: UserDomain? = null
)

// ============================================================================
// VIEWMODEL
// ============================================================================

/**
 * ViewModel for login screen.
 * Manages UI state and coordinates login flow with the domain layer.
 *
 * Responsibilities:
 * - Manage UI state (input fields, loading, errors)
 * - Normalize user input as they type
 * - Delegate validation and authentication to use case
 * - Map domain exceptions to user-friendly messages
 */
class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val googleLoginUseCase: GoogleLoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginState())
    val uiState: StateFlow<LoginState> = _uiState.asStateFlow()

    // ========================================================================
    // INPUT HANDLERS
    // ========================================================================

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
    // LOGIN OPERATION
    // ========================================================================

    /**
     * Attempts to authenticate the user.
     * Use case handles validation - ViewModel only manages UI state.
     */
    fun login() {
        val currentState = _uiState.value

        // Show loading state
        _uiState.update {
            it.copy(
                isLoading = true,
                error = null
            )
        }

        viewModelScope.launch {
            // Email already normalized from onEmailChange()
            val input = LoginInput(
                email = currentState.email,
                password = currentState.password
            )

            // Use case handles validation and authentication
            val result = loginUseCase(input)

            // Hide loading state
            _uiState.update { it.copy(isLoading = false) }

            // Update UI based on result
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        loginSuccess = true,
                        user = result.getOrNull()
                    )
                }
            } else {
                val errorMessage = mapErrorToMessage(result.exceptionOrNull())
                _uiState.update { it.copy(error = errorMessage) }
            }
        }
    }
    /**
     * Processes Google Sign-In using the ID token provided by the UI.
     * Follows the standard Munchly state-management pattern.
     */
    fun signInWithGoogle(idToken: String) {
        // 1. Show loading state and clear old errors
        _uiState.update {
            it.copy(
                isLoading = true,
                error = null
            )
        }

        viewModelScope.launch {
            try {
                // 2. Call the Google login use case
                // (Make sure to inject googleLoginUseCase into the constructor)
                val result = googleLoginUseCase(idToken)

                // 3. Hide loading state
                _uiState.update { it.copy(isLoading = false) }

                // 4. Handle results based on domain Result wrapper
                if (result.isSuccess) {
                    _uiState.update {
                        it.copy(
                            loginSuccess = true,
                            user = result.getOrNull()
                        )
                    }
                } else {
                    // Map DomainException to user-friendly String
                    val errorMessage = mapErrorToMessage(result.exceptionOrNull())
                    _uiState.update { it.copy(error = errorMessage) }
                }
            } catch (e: Exception) {
                // Safety fallback for unexpected VM-level exceptions
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "An unexpected error occurred during Google Sign-In"
                    )
                }
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