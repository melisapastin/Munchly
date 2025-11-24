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

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginState())
    val uiState: StateFlow<LoginState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null) }
    }

    fun login() {
        val currentState = _uiState.value

        val validation = validateCredentials(currentState)
        if (validation.hasErrors) {
            _uiState.update { it.copy(
                emailError = validation.emailError,
                passwordError = validation.passwordError
            ) }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val result = loginUseCase(
                email = currentState.email.trim(),
                password = currentState.password
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
                _uiState.update { it.copy(error = result.exceptionOrNull()?.message) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun validateCredentials(state: LoginState): LoginValidationResult {
        return LoginValidationResult(
            emailError = when {
                state.email.isBlank() -> "Email cannot be empty"
                !isValidEmail(state.email) -> "Please enter a valid email"
                else -> null
            },
            passwordError = when {
                state.password.isBlank() -> "Password cannot be empty"
                else -> null
            }
        )
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}

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

private data class LoginValidationResult(
    val emailError: String? = null,
    val passwordError: String? = null
) {
    val hasErrors: Boolean get() = emailError != null || passwordError != null
}