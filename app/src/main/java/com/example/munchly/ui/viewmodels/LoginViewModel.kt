package com.example.munchly.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.munchly.data.models.User
import com.example.munchly.domain.exceptions.AuthException
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

        val validation = loginUseCase.validateCredentials(
            email = currentState.email,
            password = currentState.password
        )

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
                val errorMessage = mapErrorToMessage(result.exceptionOrNull())
                _uiState.update { it.copy(error = errorMessage) }
            }
        }
    }

    private fun mapErrorToMessage(exception: Throwable?): String {
        return when (exception) {
            is AuthException.InvalidCredentials -> "Invalid email or password"
            is AuthException.UserNotFound -> "No account found with this email"
            is AuthException.NetworkError -> "Network error. Please check your connection"
            is AuthException.UserDataNotFound -> "Account data not found. Please contact support"
            else -> "An error occurred. Please try again"
        }
    }
}