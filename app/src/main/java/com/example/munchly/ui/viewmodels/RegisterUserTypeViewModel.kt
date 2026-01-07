package com.example.munchly.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.munchly.domain.models.UserTypeDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// ============================================================================
// STATE
// ============================================================================

/**
 * UI state for user type selection screen.
 * Uses domain model (UserTypeDomain) instead of data model.
 */
data class RegisterUserTypeState(
    val selectedUserType: UserTypeDomain? = null
)

// ============================================================================
// VIEWMODEL
// ============================================================================

/**
 * ViewModel for user type selection screen.
 * Simple state management for selecting between Food Lover and Restaurant Owner.
 */
class RegisterUserTypeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUserTypeState())
    val uiState: StateFlow<RegisterUserTypeState> = _uiState.asStateFlow()

    /**
     * Updates the selected user type.
     */
    fun onUserTypeSelected(userType: UserTypeDomain) {
        _uiState.update { it.copy(selectedUserType = userType) }
    }
}