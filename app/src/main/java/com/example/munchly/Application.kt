package com.example.munchly

import android.app.Application
import com.example.munchly.data.remote.LoginRemoteDataSourceImpl
import com.example.munchly.data.remote.RegisterRemoteDataSourceImpl
import com.example.munchly.data.remote.RestaurantRemoteDataSourceImpl
import com.example.munchly.data.repository.LoginRepositoryImpl
import com.example.munchly.data.repository.RegisterRepositoryImpl
import com.example.munchly.data.repository.RestaurantRepositoryImpl
import com.example.munchly.domain.repositories.RestaurantRepository
import com.example.munchly.domain.services.RestaurantService
import com.example.munchly.domain.usecases.CreateRestaurantUseCase
import com.example.munchly.domain.usecases.GetRecentReviewsUseCase
import com.example.munchly.domain.usecases.GetRestaurantByOwnerIdUseCase
import com.example.munchly.domain.usecases.GetRestaurantStatsUseCase
import com.example.munchly.domain.usecases.LoginUseCase
import com.example.munchly.domain.usecases.RegisterUseCase
import com.example.munchly.domain.usecases.UpdateRestaurantUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Application class for Munchly.
 * Handles manual dependency injection for the entire app.
 *
 * Architecture layers:
 * UI Layer → Domain Layer (Use Cases) → Data Layer (Repository) → Remote Data Source (Firebase)
 */
class MunchlyApplication : Application() {

    // ========================================================================
    // FIREBASE INSTANCES
    // ========================================================================

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    // ========================================================================
    // AUTH USE CASES
    // ========================================================================

    val loginUseCase: LoginUseCase by lazy {
        val remoteDataSource = LoginRemoteDataSourceImpl(firebaseAuth, firestore)
        val repository = LoginRepositoryImpl(remoteDataSource)
        LoginUseCase(repository)
    }

    val registerUseCase: RegisterUseCase by lazy {
        val remoteDataSource = RegisterRemoteDataSourceImpl(firebaseAuth, firestore)
        val repository = RegisterRepositoryImpl(remoteDataSource)
        RegisterUseCase(repository)
    }

    // ========================================================================
    // RESTAURANT DEPENDENCIES
    // ========================================================================

    /**
     * Restaurant repository - shared across all restaurant use cases.
     * Implements the domain layer's RestaurantRepository interface.
     */
    private val restaurantRepository: RestaurantRepository by lazy {
        val remoteDataSource = RestaurantRemoteDataSourceImpl(firestore)
        RestaurantRepositoryImpl(remoteDataSource)
    }

    /**
     * Restaurant service - contains business logic for restaurant operations.
     * Used by use cases that need validation and data transformation.
     */
    private val restaurantService: RestaurantService by lazy {
        RestaurantService()
    }

    // ========================================================================
    // RESTAURANT USE CASES
    // ========================================================================

    /**
     * Gets restaurant by owner ID.
     * Returns null if no restaurant exists for the owner.
     */
    val getRestaurantByOwnerUseCase: GetRestaurantByOwnerIdUseCase by lazy {
        GetRestaurantByOwnerIdUseCase(restaurantRepository)
    }

    /**
     * Gets restaurant statistics.
     * Returns default stats if none exist (business rule).
     */
    val getRestaurantStatsUseCase: GetRestaurantStatsUseCase by lazy {
        GetRestaurantStatsUseCase(restaurantRepository)
    }

    /**
     * Gets recent reviews for a restaurant.
     * Default limit is 5 reviews.
     */
    val getRecentReviewsUseCase: GetRecentReviewsUseCase by lazy {
        GetRecentReviewsUseCase(restaurantRepository)
    }

    /**
     * Updates an existing restaurant.
     * Validates input and preserves creation timestamp.
     */
    val updateRestaurantUseCase: UpdateRestaurantUseCase by lazy {
        UpdateRestaurantUseCase(restaurantRepository, restaurantService)
    }

    /**
     * Creates a new restaurant.
     * Validates input and generates ID.
     */
    val createRestaurantUseCase: CreateRestaurantUseCase by lazy {
        CreateRestaurantUseCase(restaurantRepository, restaurantService)
    }
}