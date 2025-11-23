package com.example.munchly.ui.viewmodels

// UI state representation for login screen
// Pure data class holding all screen state
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val loginSuccess: Boolean = false
)