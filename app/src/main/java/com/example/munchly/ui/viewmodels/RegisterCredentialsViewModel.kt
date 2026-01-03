package com.example.munchly.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.munchly.data.models.User
import com.example.munchly.data.models.UserType
import com.example.munchly.domain.exceptions.AuthException
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

        val validation = registerUseCase.validateCredentials(
            username = currentState.username,
            email = currentState.email,
            password = currentState.password
        )

        if (!validation.isValid) {
            _uiState.update {
                it.copy(
                    usernameError = validation.usernameError,
                    emailError = validation.emailError,
                    passwordError = validation.passwordError
                )
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val result = registerUseCase(
                email = currentState.email.trim(),
                password = currentState.password,
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
                val errorMessage = mapErrorToMessage(result.exceptionOrNull())
                _uiState.update { it.copy(error = errorMessage) }
            }
        }
    }

    private fun mapErrorToMessage(exception: Throwable?): String {
        return when (exception) {
            is AuthException.EmailAlreadyInUse -> "This email is already registered"
            is AuthException.UsernameAlreadyTaken -> "This username is already taken"
            is AuthException.WeakPassword -> "Password is too weak. Please use a stronger password"
            is AuthException.NetworkError -> "Network error. Please check your connection"
            else -> "Registration failed. Please try again"
        }
    }
}