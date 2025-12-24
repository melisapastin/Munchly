package com.example.munchly.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.munchly.data.models.UserType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class RegisterUserTypeState(
    val selectedUserType: UserType? = null
)

class RegisterUserTypeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUserTypeState())
    val uiState: StateFlow<RegisterUserTypeState> = _uiState.asStateFlow()

    fun onUserTypeSelected(userType: UserType) {
        _uiState.update { it.copy(selectedUserType = userType) }
    }
}