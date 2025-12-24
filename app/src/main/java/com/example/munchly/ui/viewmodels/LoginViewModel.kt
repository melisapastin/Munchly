package com.example.munchly.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.munchly.data.models.User
import com.example.munchly.domain.usecases.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val loginSuccess: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val user: User? = null
)

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginState())
    val uiState: StateFlow<LoginState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null, error = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null, error = null) }
    }

    fun login() {
        val currentState = _uiState.value

        val validation = validateCredentials(currentState.email, currentState.password)
        if (!validation.isValid) {
            _uiState.update {
                it.copy(
                    emailError = validation.emailError,
                    passwordError = validation.passwordError
                )
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val result = loginUseCase(
                email = currentState.email.trim(),
                password = currentState.password.trim()
            )

            _uiState.update { it.copy(isLoading = false) }

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        loginSuccess = true,
                        user = result.getOrNull()
                    )
                }
            } else {
                val exception = result.exceptionOrNull()
                val errorMessage = parseFirebaseError(exception)

                _uiState.update { it.copy(error = errorMessage) }
            }
        }
    }

    private fun validateCredentials(email: String, password: String): ValidationResult {
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

        return ValidationResult(
            emailError = emailError,
            passwordError = passwordError,
            isValid = emailError == null && passwordError == null
        )
    }

    private fun parseFirebaseError(exception: Throwable?): String {
        val message = exception?.message?.lowercase() ?: ""

        return when {
            // Authentication errors
            message.contains("password") ||
                    message.contains("credential") ||
                    message.contains("incorrect") ||
                    message.contains("invalid") ->
                "Invalid email or password. Please check your credentials and try again."

            // User not found
            message.contains("user") && message.contains("not found") ->
                "No account found with this email. Please sign up first."

            // Network errors
            message.contains("network") ||
                    message.contains("connection") ||
                    message.contains("timeout") ->
                "Network error. Please check your internet connection."

            // Too many attempts
            message.contains("too many") || message.contains("blocked") ->
                "Too many failed attempts. Please try again later."

            // Email format
            message.contains("email") && message.contains("badly formatted") ->
                "Please enter a valid email address."

            // Generic fallback
            else ->
                "Unable to sign in. Please try again."
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private data class ValidationResult(
        val emailError: String?,
        val passwordError: String?,
        val isValid: Boolean
    )
}