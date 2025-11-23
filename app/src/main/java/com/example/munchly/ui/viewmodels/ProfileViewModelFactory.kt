package com.example.munchly.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.munchly.data.remote.BookmarkRemoteDataSource
import com.example.munchly.data.remote.ReviewRemoteDataSource
import com.example.munchly.data.remote.UserRemoteDataSource
import com.example.munchly.data.repository.AuthRepository
import com.example.munchly.data.repository.BookmarkRepository
import com.example.munchly.data.repository.ReviewRepository
import com.example.munchly.data.repository.UserProfileRepository
import com.example.munchly.domain.usecases.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            // Build dependency tree
            val firebaseAuth = FirebaseAuth.getInstance()
            val firestore = FirebaseFirestore.getInstance()

            // Data sources
            val userRemoteDataSource = UserRemoteDataSource(firestore)
            val bookmarkRemoteDataSource = BookmarkRemoteDataSource(firestore)
            val reviewRemoteDataSource = ReviewRemoteDataSource(firestore)

            // Repositories
            val authRepository = AuthRepository(firebaseAuth, userRemoteDataSource)
            val bookmarkRepository = BookmarkRepository(bookmarkRemoteDataSource)
            val reviewRepository = ReviewRepository(reviewRemoteDataSource)
            val userProfileRepository = UserProfileRepository(
                userRemoteDataSource,
                bookmarkRemoteDataSource,
                reviewRemoteDataSource
            )

            // Use cases
            val getUserProfileUseCase = GetUserProfileUseCase(userProfileRepository)
            val signOutUseCase = SignOutUseCase(authRepository)
            val getCurrentUserUseCase = GetCurrentUserUseCase(authRepository)

            return ProfileViewModel(
                getUserProfileUseCase = getUserProfileUseCase,
                signOutUseCase = signOutUseCase,
                getCurrentUserUseCase = getCurrentUserUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}