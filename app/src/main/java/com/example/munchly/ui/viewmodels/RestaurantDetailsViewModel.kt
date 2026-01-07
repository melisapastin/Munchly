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
// VIEWMODEL - COMPLETELY FIXED FOR NEW ACHIEVEMENT LOGIC
// ============================================================================

/**
 * FIXED: Now properly tracks stats based on new requirements:
 * - totalReviews: Count of ALL reviews with text (not unique)
 * - totalRatings: Count of ALL ratings with stars ≥1 and ≤5 (not unique)
 * - uniqueBookmarkedRestaurants: Set of currently bookmarked restaurant IDs
 * - uniqueVeganRestaurantsRated: Set of unique vegan restaurant IDs rated
 * - uniqueRestaurantsVisitedSet: Set of unique restaurant IDs visited (review OR rating)
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

    /**
     * FIXED: Now updates uniqueBookmarkedRestaurants Set correctly
     */
    fun toggleBookmark() {
        viewModelScope.launch {
            val result = toggleBookmarkUseCase(userId, restaurantId)
            if (result.isSuccess) {
                val isBookmarked = result.getOrNull() ?: false
                _uiState.update { it.copy(isBookmarked = isBookmarked) }

                // Update stats with correct Set tracking
                updateUserStatsUseCase(userId) { stats ->
                    if (isBookmarked) {
                        // Add restaurant to bookmarked set
                        stats.copy(
                            uniqueBookmarkedRestaurants = stats.uniqueBookmarkedRestaurants + restaurantId,
                            totalBookmarks = stats.uniqueBookmarkedRestaurants.size + 1  // Update deprecated field
                        )
                    } else {
                        // Remove restaurant from bookmarked set
                        stats.copy(
                            uniqueBookmarkedRestaurants = stats.uniqueBookmarkedRestaurants - restaurantId,
                            totalBookmarks = (stats.uniqueBookmarkedRestaurants.size - 1).coerceAtLeast(0)
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
     * COMPLETELY REWRITTEN: Tracks stats according to new requirements
     *
     * Rules:
     * 1. Culinary Critic: Increment totalReviews ONLY if comment exists
     * 2. Rating Expert: Increment totalRatings ONLY if rating ≥1 and ≤5
     * 3. Bookmark Collector: Track in uniqueBookmarkedRestaurants Set
     * 4. Veggie Lover: Add to uniqueVeganRestaurantsRated Set ONLY if rated AND vegan
     * 5. Foodie Explorer: Add to uniqueRestaurantsVisitedSet if review OR rating
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

                // Update restaurant stats
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

                // Get restaurant info for vegan check
                val restaurant = _uiState.value.restaurant
                val isVegan = restaurant?.tags?.any {
                    it.lowercase() == "vegan"
                } ?: false

                // Determine what was submitted
                val hasComment = comment.trim().isNotEmpty()
                val hasValidRating = rating >= 1.0 && rating <= 5.0

                Log.d("RestaurantDetailsVM", "Stats update - hasComment: $hasComment, hasValidRating: $hasValidRating, isVegan: $isVegan")

                // Update user stats with new logic
                updateUserStatsUseCase(userId) { stats ->
                    Log.d("RestaurantDetailsVM", "Current stats - " +
                            "totalReviews: ${stats.totalReviews}, " +
                            "totalRatings: ${stats.totalRatings}, " +
                            "uniqueVisited: ${stats.uniqueRestaurantsVisitedSet.size}, " +
                            "uniqueVegan: ${stats.uniqueVeganRestaurantsRated.size}")

                    // 1. CULINARY CRITIC: Increment totalReviews if comment exists
                    val newTotalReviews = if (hasComment) {
                        stats.totalReviews + 1
                    } else {
                        stats.totalReviews
                    }

                    // 2. RATING EXPERT: Increment totalRatings if rating is valid (≥1 and ≤5)
                    val newTotalRatings = if (hasValidRating) {
                        stats.totalRatings + 1
                    } else {
                        stats.totalRatings
                    }

                    // 4. VEGGIE LOVER: Add to uniqueVeganRestaurantsRated if vegan AND rated
                    val newUniqueVeganRated = if (isVegan && hasValidRating) {
                        stats.uniqueVeganRestaurantsRated + restaurantId
                    } else {
                        stats.uniqueVeganRestaurantsRated
                    }

                    // 5. FOODIE EXPLORER: Add to uniqueRestaurantsVisitedSet if review OR rating
                    val newUniqueVisited = if (hasComment || hasValidRating) {
                        stats.uniqueRestaurantsVisitedSet + restaurantId
                    } else {
                        stats.uniqueRestaurantsVisitedSet
                    }

                    val updatedStats = stats.copy(
                        totalReviews = newTotalReviews,
                        totalRatings = newTotalRatings,
                        uniqueVeganRestaurantsRated = newUniqueVeganRated,
                        uniqueRestaurantsVisitedSet = newUniqueVisited,
                        // Update deprecated fields for backward compatibility
                        veganRestaurantsRated = newUniqueVeganRated.size,
                        uniqueRestaurantsVisited = newUniqueVisited.size
                    )

                    Log.d("RestaurantDetailsVM", "New stats - " +
                            "totalReviews: ${updatedStats.totalReviews}, " +
                            "totalRatings: ${updatedStats.totalRatings}, " +
                            "uniqueVisited: ${updatedStats.uniqueRestaurantsVisitedSet.size}, " +
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