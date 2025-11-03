package com.example.munchly.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.munchly.ui.screens.UserType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RegisterViewModel : ViewModel() {

    // UI State: User type selection
    private val _selectedUserType = MutableStateFlow<UserType?>(null)
    val selectedUserType: StateFlow<UserType?> = _selectedUserType.asStateFlow()

    // UI State: Registration step (user type selection or credentials)
    private val _showCredentialsScreen = MutableStateFlow(false)
    val showCredentialsScreen: StateFlow<Boolean> = _showCredentialsScreen.asStateFlow()

    // UI State: User credentials
    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    // UI State Updates: User type selection
    fun onUserTypeSelected(userType: UserType) {
        _selectedUserType.value = userType
    }

    // UI State Updates: Navigation
    fun onContinueToCredentials() {
        _showCredentialsScreen.value = true
    }

    fun onBackToUserTypeSelection() {
        _showCredentialsScreen.value = false
    }

    // UI State Updates: Credentials input
    fun onUsernameChange(newUsername: String) {
        _username.value = newUsername
    }

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    // Button clicks: Just forward to whoever is listening (MainActivity will handle)
    // No logic here - just state management
}