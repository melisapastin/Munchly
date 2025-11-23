package com.example.munchly.ui.viewmodels

import com.example.munchly.data.models.UserType

// UI state representation for registration screens
// Pure data class holding all registration flow state
data class RegisterUiState(
    val selectedUserType: UserType? = null,
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val registrationSuccess: Boolean = false
)