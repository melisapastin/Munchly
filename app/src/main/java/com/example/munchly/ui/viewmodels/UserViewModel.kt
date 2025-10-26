package com.example.munchly.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.munchly.data.fire.FirebaseDataSource
import com.example.munchly.data.repository.UserRepository
import com.example.munchly.domain.managers.UserManager
import com.example.munchly.domain.usecases.GetUserUseCase
import com.example.munchly.data.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val useCase = GetUserUseCase(
        UserManager(UserRepository(FirebaseDataSource()))
    )

    private val _userState = MutableStateFlow<User?>(null)
    val userState: StateFlow<User?> get() = _userState

    fun loadUser(id: String) {
        viewModelScope.launch {
            _userState.value = useCase.execute(id)
        }
    }
}
