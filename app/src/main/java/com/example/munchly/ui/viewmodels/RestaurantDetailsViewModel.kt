package com.example.munchly.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.munchly.domain.exceptions.DomainException
import com.example.munchly.domain.models.RestaurantDomain
import com.example.munchly.domain.models.ReviewInput
import com.example.munchly.domain.usecases.CreateReviewUseCase
import com.example.munchly.domain.usecases.GetRestaurantDetailsUseCase
import com.example.munchly.domain.usecases.GetRestaurantReviewsUseCase
import com.example.munchly.domain.usecases.HasUserReviewedRestaurantUseCase
import com.example.munchly.domain.usecases.IncrementRestaurantStatsUseCase
import com.example.munchly.domain.usecases.IncrementRestaurantViewsUseCase
import com.example.munchly.domain.usecases.IsRestaurantBookmarkedUseCase
import com.example.munchly.domain.usecases.ToggleBookmarkUseCase
import com.example.munchly.domain.usecases.UpdateUserStatsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ============================================================================
// STATE
// ============================================================================

data class RestaurantDetailsState(
    val isLoading: Boolean = true,
    val restaurant: RestaurantDomain? = null,
    val averageRating: Double = 0.0,
    val totalReviews: Int = 0,
    val isBookmarked: Boolean = false,
    val hasUserReviewed: Boolean = false,
    val showWriteReviewDialog: Boolean = false,
    val error: String? = null
)

// ============================================================================
// VIEWMODEL - FIXED USER STATS TRACKING
// ============================================================================

/**
 * FIXED: Now properly tracks:
 * - totalRatings: Only ratings > 0
 * - uniqueRestaurantsVisited: Set of unique restaurant IDs
 * - uniqueVeganRestaurantsRated: Set of unique vegan restaurant IDs (only with ratings > 0)
 */
class RestaurantDetailsViewModel(
    private val restaurantId: String,
    private val userId: String,
    private val username: String,
    private val getRestaurantDetailsUseCase: GetRestaurantDetailsUseCase,
    private val getRestaurantReviewsUseCase: GetRestaurantReviewsUseCase,
    private val isRestaurantBookmarkedUseCase: IsRestaurantBookmarkedUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase,
    private val hasUserReviewedRestaurantUseCase: HasUserReviewedRestaurantUseCase,
    private val createReviewUseCase: CreateReviewUseCase,
    private val updateUserStatsUseCase: UpdateUserStatsUseCase,
    private val incrementRestaurantStatsUseCase: IncrementRestaurantStatsUseCase,
    private val incrementRestaurantViewsUseCase: IncrementRestaurantViewsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RestaurantDetailsState())
    val uiState: StateFlow<RestaurantDetailsState> = _uiState.asStateFlow()

    init {
        loadRestaurantDetails()
        checkBookmarkStatus()
        checkReviewStatus()
        incrementViews()
    }

    private fun incrementViews() {
        viewModelScope.launch {
            incrementRestaurantViewsUseCase(restaurantId)
        }
    }

    fun loadRestaurantDetails() {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            Log.d("RestaurantDetailsVM", "Loading details for restaurant: $restaurantId")

            val detailsResult = getRestaurantDetailsUseCase(restaurantId)
            if (detailsResult.isFailure) {
                Log.e("RestaurantDetailsVM", "Failed to load details", detailsResult.exceptionOrNull())
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = mapErrorToMessage(detailsResult.exceptionOrNull())
                    )
                }
                return@launch
            }

            val details = detailsResult.getOrNull()
            if (details == null) {
                Log.e("RestaurantDetailsVM", "Details were null")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Restaurant not found"
                    )
                }
                return@launch
            }

            val reviewsResult = getRestaurantReviewsUseCase(restaurantId, limit = null)
            val reviews = reviewsResult.getOrNull() ?: emptyList()

            val reviewsWithRatings = reviews.filter { it.rating > 0.0 }
            val averageRating = if (reviewsWithRatings.isNotEmpty()) {
                reviewsWithRatings.map { it.rating }.average()
            } else {
                0.0
            }

            val reviewsWithComments = reviews.filter { it.comment.isNotBlank() }
            val totalReviews = reviewsWithComments.size

            Log.d("RestaurantDetailsVM", "Successfully loaded: ${details.restaurant.name}, " +
                    "calculated rating: $averageRating (from ${reviewsWithRatings.size} ratings), " +
                    "reviews: $totalReviews")

            _uiState.update {
                it.copy(
                    isLoading = false,
                    restaurant = details.restaurant,
                    averageRating = averageRating,
                    totalReviews = totalReviews,
                    error = null
                )
            }
        }
    }

    private fun checkBookmarkStatus() {
        viewModelScope.launch {
            val result = isRestaurantBookmarkedUseCase(userId, restaurantId)
            if (result.isSuccess) {
                val isBookmarked = result.getOrNull() ?: false
                _uiState.update { it.copy(isBookmarked = isBookmarked) }
            }
        }
    }

    private fun checkReviewStatus() {
        viewModelScope.launch {
            val result = hasUserReviewedRestaurantUseCase(userId, restaurantId)
            if (result.isSuccess) {
                _uiState.update { it.copy(hasUserReviewed = false) }
            }
        }
    }

    fun toggleBookmark() {
        viewModelScope.launch {
            val result = toggleBookmarkUseCase(userId, restaurantId)
            if (result.isSuccess) {
                val isBookmarked = result.getOrNull() ?: false
                _uiState.update { it.copy(isBookmarked = isBookmarked) }

                if (isBookmarked) {
                    updateUserStatsUseCase(userId) { stats ->
                        stats.copy(totalBookmarks = stats.totalBookmarks + 1)
                    }
                } else {
                    updateUserStatsUseCase(userId) { stats ->
                        stats.copy(
                            totalBookmarks = (stats.totalBookmarks - 1).coerceAtLeast(0)
                        )
                    }
                }
            }
        }
    }

    fun showWriteReviewDialog() {
        if (!_uiState.value.hasUserReviewed) {
            _uiState.update { it.copy(showWriteReviewDialog = true) }
        }
    }

    fun hideWriteReviewDialog() {
        _uiState.update { it.copy(showWriteReviewDialog = false) }
    }

    /**
     * FIXED: Properly tracks stats based on rating value
     */
    fun submitReview(
        rating: Double,
        comment: String,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            Log.d("RestaurantDetailsVM", "Submitting review: rating=$rating, comment='$comment'")

            val input = ReviewInput(
                restaurantId = restaurantId,
                userId = userId,
                userName = username,
                rating = rating,
                comment = comment.trim()
            )

            val result = createReviewUseCase(input)

            if (result.isSuccess) {
                val createdReview = result.getOrNull()
                Log.d("RestaurantDetailsVM", "Review created successfully with ID: ${createdReview?.id}")

                val statsResult = incrementRestaurantStatsUseCase(
                    restaurantId = restaurantId,
                    newRating = rating,
                    hasComment = comment.trim().isNotEmpty()
                )

                if (statsResult.isFailure) {
                    Log.e("RestaurantDetailsVM", "Failed to update stats", statsResult.exceptionOrNull())
                }

                _uiState.update {
                    it.copy(
                        showWriteReviewDialog = false,
                        hasUserReviewed = false
                    )
                }

                // FIXED: Update user stats with proper logic
                val restaurant = _uiState.value.restaurant
                val isVegan = restaurant?.tags?.any {
                    it.lowercase() == "vegan"
                } ?: false

                val hasComment = comment.trim().isNotEmpty()
                val hasRating = rating > 0.0

                Log.d("RestaurantDetailsVM", "Updating stats - hasComment: $hasComment, hasRating: $hasRating, isVegan: $isVegan")

                updateUserStatsUseCase(userId) { stats ->
                    Log.d("RestaurantDetailsVM", "Current stats - " +
                            "totalReviews: ${stats.totalReviews}, " +
                            "totalRatings: ${stats.totalRatings}, " +
                            "uniqueVisited: ${stats.uniqueRestaurantsVisited}, " +
                            "uniqueWithRatings: ${stats.uniqueRestaurantsWithRatings.size}, " +
                            "uniqueVegan: ${stats.uniqueVeganRestaurantsRated.size}")

                    // Increment totalReviews ONLY if comment exists
                    val newTotalReviews = if (hasComment) {
                        stats.totalReviews + 1
                    } else {
                        stats.totalReviews
                    }

                    // Increment totalRatings ONLY if rating > 0
                    val newTotalRatings = if (hasRating) {
                        stats.totalRatings + 1
                    } else {
                        stats.totalRatings
                    }

                    // Add to uniqueRestaurantsWithRatings ONLY if rating > 0
                    val newUniqueWithRatings = if (hasRating) {
                        stats.uniqueRestaurantsWithRatings + restaurantId
                    } else {
                        stats.uniqueRestaurantsWithRatings
                    }

                    // Add to uniqueVeganRestaurantsRated ONLY if vegan AND rating > 0
                    val newUniqueVeganRated = if (isVegan && hasRating) {
                        stats.uniqueVeganRestaurantsRated + restaurantId
                    } else {
                        stats.uniqueVeganRestaurantsRated
                    }

                    // FIXED: Add restaurant to uniqueVisitedRestaurants Set
                    // This ensures Foodie Explorer counts unique restaurants
                    val newUniqueVisitedSet = if (hasComment || hasRating) {
                        // Count as "visited" if they left a review OR rating
                        stats.uniqueVisitedRestaurants + restaurantId
                    } else {
                        stats.uniqueVisitedRestaurants
                    }

                    val newUniqueVisitedCount = newUniqueVisitedSet.size

                    val updatedStats = stats.copy(
                        totalReviews = newTotalReviews,
                        totalRatings = newTotalRatings,
                        uniqueRestaurantsVisited = newUniqueVisitedCount,
                        uniqueRestaurantsWithRatings = newUniqueWithRatings,
                        uniqueVeganRestaurantsRated = newUniqueVeganRated,
                        uniqueVisitedRestaurants = newUniqueVisitedSet,  // ADD THIS
                        // Keep old fields for backward compatibility
                        totalBookmarks = stats.totalBookmarks,
                        veganRestaurantsRated = newUniqueVeganRated.size
                    )

                    Log.d("RestaurantDetailsVM", "New stats - " +
                            "totalReviews: ${updatedStats.totalReviews}, " +
                            "totalRatings: ${updatedStats.totalRatings}, " +
                            "uniqueVisited: ${updatedStats.uniqueRestaurantsVisited}, " +
                            "uniqueWithRatings: ${updatedStats.uniqueRestaurantsWithRatings.size}, " +
                            "uniqueVegan: ${updatedStats.uniqueVeganRestaurantsRated.size}")

                    updatedStats
                }

                loadRestaurantDetails()
            } else {
                val error = result.exceptionOrNull()
                Log.e("RestaurantDetailsVM", "Failed to create review", error)
                onError(mapErrorToMessage(error))
            }
        }
    }

    private fun mapErrorToMessage(exception: Throwable?): String {
        return when (exception) {
            is DomainException.NetworkError ->
                "Network error. Please check your connection"
            is DomainException.ValidationError ->
                exception.reason
            is DomainException.ResourceNotFound ->
                "Restaurant not found"
            is DomainException.PermissionDenied ->
                "Permission denied. Please try logging in again"
            else ->
                "An error occurred: ${exception?.message ?: "Unknown error"}"
        }
    }
}