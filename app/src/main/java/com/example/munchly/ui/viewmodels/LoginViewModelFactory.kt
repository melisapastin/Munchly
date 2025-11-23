package com.example.munchly.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.munchly.data.remote.UserRemoteDataSource
import com.example.munchly.data.repository.AuthRepository
import com.example.munchly.domain.usecases.LoginUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            // Build dependency tree
            val firebaseAuth = FirebaseAuth.getInstance()
            val firestore = FirebaseFirestore.getInstance()
            val userRemoteDataSource = UserRemoteDataSource(firestore)
            val authRepository = AuthRepository(firebaseAuth, userRemoteDataSource)
            val loginUseCase = LoginUseCase(authRepository)

            return LoginViewModel(loginUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}