package com.example.munchly.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.munchly.data.remote.BookmarkRemoteDataSource
import com.example.munchly.data.remote.RestaurantRemoteDataSource
import com.example.munchly.data.remote.UserRemoteDataSource
import com.example.munchly.data.repository.AuthRepository
import com.example.munchly.data.repository.BookmarkRepository
import com.example.munchly.data.repository.RestaurantRepository
import com.example.munchly.domain.usecases.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FoodLoverFeedViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FoodLoverFeedViewModel::class.java)) {
            // Build dependency tree using your existing backend
            val firebaseAuth = FirebaseAuth.getInstance()
            val firestore = FirebaseFirestore.getInstance()

            // Data sources
            val restaurantRemoteDataSource = RestaurantRemoteDataSource(firestore)
            val bookmarkRemoteDataSource = BookmarkRemoteDataSource(firestore)
            val userRemoteDataSource = UserRemoteDataSource(firestore)

            // Repositories
            val restaurantRepository = RestaurantRepository(restaurantRemoteDataSource)
            val bookmarkRepository = BookmarkRepository(bookmarkRemoteDataSource)
            val authRepository = AuthRepository(firebaseAuth, userRemoteDataSource)

            // Use cases
            val getRestaurantsUseCase = GetRestaurantsUseCase(restaurantRepository)
            val searchRestaurantsUseCase = SearchRestaurantsUseCase(restaurantRepository)
            val toggleBookmarkUseCase = ToggleBookmarkUseCase(bookmarkRepository)
            val getCurrentUserUseCase = GetCurrentUserUseCase(authRepository)
            val isBookmarkedUseCase = IsBookmarkedUseCase(bookmarkRepository)

            return FoodLoverFeedViewModel(
                getRestaurantsUseCase = getRestaurantsUseCase,
                searchRestaurantsUseCase = searchRestaurantsUseCase,
                toggleBookmarkUseCase = toggleBookmarkUseCase,
                getCurrentUserUseCase = getCurrentUserUseCase,
                isBookmarkedUseCase = isBookmarkedUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}