package com.example.munchly.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.munchly.data.models.User
import com.example.munchly.data.models.UserType
import com.example.munchly.domain.usecases.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterCredentialsState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val registrationSuccess: Boolean = false,
    val usernameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val user: User? = null
)

class RegisterCredentialsViewModel(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterCredentialsState())
    val uiState: StateFlow<RegisterCredentialsState> = _uiState.asStateFlow()

    fun onUsernameChange(username: String) {
        _uiState.update { it.copy(username = username, usernameError = null, error = null) }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null, error = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null, error = null) }
    }

    fun register(userType: UserType) {
        val currentState = _uiState.value

        val validation = validateCredentials(currentState)
        if (!validation.isValid) {
            _uiState.update { it.copy(
                usernameError = validation.usernameError,
                emailError = validation.emailError,
                passwordError = validation.passwordError
            ) }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val result = registerUseCase(
                email = currentState.email.trim(),
                password = currentState.password.trim(),
                username = currentState.username.trim(),
                userType = userType
            )

            _uiState.update { it.copy(isLoading = false) }

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        registrationSuccess = true,
                        user = result.getOrNull()
                    )
                }
            } else {
                _uiState.update { it.copy(error = result.exceptionOrNull()?.message) }
            }
        }
    }

    private fun validateCredentials(state: RegisterCredentialsState): ValidationResult {
        val usernameError = when {
            state.username.isBlank() -> "Username cannot be empty"
            state.username.length < 3 -> "Username must be at least 3 characters"
            state.username.length > 20 -> "Username must be less than 20 characters"
            !state.username.matches(Regex("^[a-zA-Z0-9_]+$")) ->
                "Username can only contain letters, numbers and underscores"
            else -> null
        }

        val emailError = when {
            state.email.isBlank() -> "Email cannot be empty"
            !isValidEmail(state.email) -> "Please enter a valid email"
            else -> null
        }

        val passwordError = when {
            state.password.isBlank() -> "Password cannot be empty"
            state.password.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }

        return ValidationResult(
            usernameError = usernameError,
            emailError = emailError,
            passwordError = passwordError,
            isValid = usernameError == null && emailError == null && passwordError == null
        )
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private data class ValidationResult(
        val usernameError: String?,
        val emailError: String?,
        val passwordError: String?,
        val isValid: Boolean
    )
}