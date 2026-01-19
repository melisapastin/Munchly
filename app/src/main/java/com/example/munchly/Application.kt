package com.example.munchly

import android.app.Application
import com.example.munchly.data.remote.AuthRemoteDataSourceImpl
import com.example.munchly.data.remote.RestaurantRemoteDataSourceImpl
import com.example.munchly.data.repository.AuthRepositoryImpl
import com.example.munchly.data.repository.RestaurantRepositoryImpl
import com.example.munchly.domain.models.UserDomain
import com.example.munchly.domain.repository.AuthRepository
import com.example.munchly.domain.repositories.RestaurantRepository
import com.example.munchly.domain.services.RestaurantService
import com.example.munchly.domain.usecases.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.munchly.domain.usecases.SearchRestaurantsUseCase
import com.example.munchly.domain.usecases.ToggleBookmarkUseCase
import com.example.munchly.data.remote.AchievementRemoteDataSourceImpl
import com.example.munchly.data.remote.BookmarkRemoteDataSourceImpl
import com.example.munchly.data.remote.RestaurantSearchDataSourceImpl
import com.example.munchly.data.remote.ReviewRemoteDataSourceImpl
import com.example.munchly.data.repository.AchievementRepositoryImpl
import com.example.munchly.data.repository.BookmarkRepositoryImpl
import com.example.munchly.data.repository.RestaurantSearchRepositoryImpl
import com.example.munchly.data.repository.ReviewRepositoryImpl
import com.example.munchly.domain.repositories.AchievementRepository
import com.example.munchly.domain.repositories.BookmarkRepository
import com.example.munchly.domain.repositories.RestaurantSearchRepository
import com.example.munchly.domain.repositories.ReviewRepository
import com.example.munchly.domain.services.AchievementService
import com.google.firebase.storage.FirebaseStorage
import com.example.munchly.data.storage.FirebaseStorageService
import com.example.munchly.domain.services.StorageService
import com.example.munchly.domain.usecases.UploadMenuPdfUseCase
import com.example.munchly.domain.usecases.UploadRestaurantImageUseCase

/**
 * Application class for Munchly.
 * Handles manual dependency injection for the entire app.
 *
 * Architecture layers:
 * UI Layer â†’ Domain Layer (Use Cases) â†’ Data Layer (Repository) â†’ Remote Data Source (Firebase)
 */
class MunchlyApplication : Application() {

    // ========================================================================
    // FIREBASE INSTANCES
    // ========================================================================

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    val googleLoginUseCase by lazy {
        GoogleLoginUseCase(authRepository)
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    // ========================================================================
    // AUTH DEPENDENCIES
    // ========================================================================

    /**
     * Auth repository - shared across all auth use cases.
     * Implements the domain layer's AuthRepository interface.
     */
    private val authRepository: AuthRepository by lazy {
        val remoteDataSource = AuthRemoteDataSourceImpl(firebaseAuth, firestore)
        AuthRepositoryImpl(remoteDataSource)
    }

    // ========================================================================
    // AUTH USE CASES
    // ========================================================================

    /**
     * Authenticates a user with email and password.
     * Validates input before delegating to repository.
     */
    val loginUseCase: LoginUseCase by lazy {
        LoginUseCase(authRepository)
    }

    /**
     * Creates a new user account.
     * Validates input and checks for duplicate username.
     */
    val registerUseCase: RegisterUseCase by lazy {
        RegisterUseCase(authRepository)
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

    val incrementRestaurantStatsUseCase: IncrementRestaurantStatsUseCase by lazy {
        IncrementRestaurantStatsUseCase(restaurantRepository)
    }

    val incrementRestaurantViewsUseCase: IncrementRestaurantViewsUseCase by lazy {
        IncrementRestaurantViewsUseCase(restaurantRepository)
    }

    val countRestaurantBookmarksUseCase: CountRestaurantBookmarksUseCase by lazy {
        CountRestaurantBookmarksUseCase(bookmarkRepository)
    }

    // ========================================================================
    // USER SESSION
    // ========================================================================

    /**
     * Current authenticated user session.
     * Null if no user is logged in.
     */
    var currentUser: UserDomain? = null
        private set

    /**
     * Sets the current authenticated user.
     * Called after successful login or registration.
     */
    fun setCurrentUser(user: UserDomain) {
        currentUser = user
    }

    /**
     * Clears the current user session.
     * Called on logout.
     */
    fun clearCurrentUser() {
        currentUser = null
    }

    // ========================================================================
    // FOOD LOVER DEPENDENCIES
    // ========================================================================

    private val restaurantSearchRepository: RestaurantSearchRepository by lazy {
        val remoteDataSource = RestaurantSearchDataSourceImpl(firestore)
        RestaurantSearchRepositoryImpl(remoteDataSource)
    }

    private val bookmarkRepository: BookmarkRepository by lazy {
        val remoteDataSource = BookmarkRemoteDataSourceImpl(firestore)
        BookmarkRepositoryImpl(remoteDataSource)
    }

    private val reviewRepository: ReviewRepository by lazy {
        val remoteDataSource = ReviewRemoteDataSourceImpl(firestore)
        ReviewRepositoryImpl(remoteDataSource)
    }

    private val achievementRepository: AchievementRepository by lazy {
        val remoteDataSource = AchievementRemoteDataSourceImpl(firestore)
        AchievementRepositoryImpl(remoteDataSource)
    }

    private val achievementService: AchievementService by lazy {
        AchievementService()
    }

    // Food Lover Use Cases - Restaurant Discovery
    val getAllRestaurantsUseCase: GetAllRestaurantsUseCase by lazy {
        GetAllRestaurantsUseCase(restaurantSearchRepository)
    }

    val searchRestaurantsUseCase: SearchRestaurantsUseCase by lazy {
        SearchRestaurantsUseCase(restaurantSearchRepository)
    }

    val getRestaurantDetailsUseCase: GetRestaurantDetailsUseCase by lazy {
        GetRestaurantDetailsUseCase(restaurantSearchRepository, restaurantRepository)
    }

    // Food Lover Use Cases - Bookmarks
    val getUserBookmarksUseCase: GetUserBookmarksUseCase by lazy {
        GetUserBookmarksUseCase(bookmarkRepository)
    }

    val toggleBookmarkUseCase: ToggleBookmarkUseCase by lazy {
        ToggleBookmarkUseCase(bookmarkRepository)
    }

    val isRestaurantBookmarkedUseCase: IsRestaurantBookmarkedUseCase by lazy {
        IsRestaurantBookmarkedUseCase(bookmarkRepository)
    }

    // Food Lover Use Cases - Reviews
    val createReviewUseCase: CreateReviewUseCase by lazy {
        CreateReviewUseCase(reviewRepository)
    }

    val getRestaurantReviewsUseCase: GetRestaurantReviewsUseCase by lazy {
        GetRestaurantReviewsUseCase(reviewRepository)
    }

    val hasUserReviewedRestaurantUseCase: HasUserReviewedRestaurantUseCase by lazy {
        HasUserReviewedRestaurantUseCase(reviewRepository)
    }

    // Food Lover Use Cases - Achievements
    val getUserAchievementsUseCase: GetUserAchievementsUseCase by lazy {
        GetUserAchievementsUseCase(achievementRepository, achievementService)
    }

    val updateUserStatsUseCase: UpdateUserStatsUseCase by lazy {
        UpdateUserStatsUseCase(achievementRepository, achievementService)
    }

    // ========================================================================
    // FIREBASE STORAGE
    // ========================================================================

    private val firebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }

    // ========================================================================
    // STORAGE SERVICE
    // ========================================================================

    private val storageService: StorageService by lazy {
        FirebaseStorageService(
            storage = firebaseStorage,
            contentResolver = contentResolver
        )
    }

    // ========================================================================
    // STORAGE USE CASES
    // ========================================================================

    val uploadMenuPdfUseCase: UploadMenuPdfUseCase by lazy {
        UploadMenuPdfUseCase(storageService)
    }

    val uploadRestaurantImageUseCase: UploadRestaurantImageUseCase by lazy {
        UploadRestaurantImageUseCase(storageService)
    }
}