package com.example.munchly.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.munchly.data.remote.UserRemoteDataSource
import com.example.munchly.data.repository.AuthRepository
import com.example.munchly.domain.usecases.RegisterUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            // Build dependency tree following same pattern as login
            val firebaseAuth = FirebaseAuth.getInstance()
            val firestore = FirebaseFirestore.getInstance()
            val userRemoteDataSource = UserRemoteDataSource(firestore)
            val authRepository = AuthRepository(firebaseAuth, userRemoteDataSource)
            val registerUseCase = RegisterUseCase(authRepository)

            return RegisterViewModel(registerUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}