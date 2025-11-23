package com.example.munchly.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.munchly.data.models.UserProfile
import com.example.munchly.domain.usecases.GetUserProfileUseCase
import com.example.munchly.domain.usecases.SignOutUseCase
import com.example.munchly.domain.usecases.GetCurrentUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            try {
                val currentUser = getCurrentUserUseCase()
                currentUser?.let { user ->
                    val userProfile = getUserProfileUseCase(user.uid)
                    _uiState.value = _uiState.value.copy(
                        userProfile = userProfile,
                        isLoading = false
                    )
                } ?: run {
                    _uiState.value = _uiState.value.copy(
                        error = "User not found",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to load profile",
                    isLoading = false
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                signOutUseCase()
                _uiState.value = _uiState.value.copy(logoutSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Logout failed")
            }
        }
    }
}

data class ProfileUiState(
    val userProfile: UserProfile? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val logoutSuccess: Boolean = false
)