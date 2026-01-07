package com.example.munchly.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.munchly.domain.exceptions.DomainException
import com.example.munchly.domain.models.RestaurantDomain
import com.example.munchly.domain.models.RestaurantStatsDomain
import com.example.munchly.domain.models.ReviewDomain
import com.example.munchly.domain.usecases.GetRecentReviewsUseCase
import com.example.munchly.domain.usecases.GetRestaurantByOwnerIdUseCase
import com.example.munchly.domain.usecases.GetRestaurantStatsUseCase
import com.example.munchly.domain.usecases.GetRestaurantReviewsUseCase
import com.example.munchly.domain.usecases.CountRestaurantBookmarksUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ============================================================================
// STATE
// ============================================================================

/**
 * FIXED: Now includes real bookmark count from database
 */
data class OwnerFeedState(
    val isLoading: Boolean = true,
    val restaurant: RestaurantDomain? = null,
    val stats: RestaurantStatsDomain? = null,
    val recentReviews: List<ReviewDomain> = emptyList(),
    val calculatedAverageRating: Double = 0.0,
    val calculatedTotalRatings: Int = 0,
    val calculatedTotalReviews: Int = 0,
    val actualBookmarkCount: Int = 0,  // ADDED: Real count from database
    val error: String? = null,
    val hasRestaurant: Boolean = false
)

// ============================================================================
// VIEWMODEL - FIXED
// ============================================================================

/**
 * FIXED: Now counts actual bookmarks from database instead of using cached stats
 */
class OwnerFeedViewModel(
    private val ownerId: String,
    private val getRestaurantByOwnerUseCase: GetRestaurantByOwnerIdUseCase,
    private val getRestaurantStatsUseCase: GetRestaurantStatsUseCase,
    private val getRecentReviewsUseCase: GetRecentReviewsUseCase,
    private val getRestaurantReviewsUseCase: GetRestaurantReviewsUseCase,
    private val countRestaurantBookmarksUseCase: CountRestaurantBookmarksUseCase  // ADDED
) : ViewModel() {

    private val _uiState = MutableStateFlow(OwnerFeedState())
    val uiState: StateFlow<OwnerFeedState> = _uiState.asStateFlow()

    init {
        loadRestaurantData()
    }

    /**
     * FIXED: Now loads real bookmark count from database
     */
    fun loadRestaurantData() {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            // Load restaurant first
            val restaurantResult = getRestaurantByOwnerUseCase(ownerId)

            if (restaurantResult.isFailure) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = mapErrorToMessage(restaurantResult.exceptionOrNull())
                    )
                }
                return@launch
            }

            val restaurant = restaurantResult.getOrNull()

            if (restaurant == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        hasRestaurant = false
                    )
                }
                return@launch
            }

            // Load stats (for views only, bookmarks will be counted separately)
            val statsResult = getRestaurantStatsUseCase(restaurant.id)
            val stats = statsResult.getOrNull()

            // Load ALL reviews to calculate correct average
            val reviewsResult = getRestaurantReviewsUseCase(restaurant.id, limit = null)
            val allReviews = reviewsResult.getOrNull() ?: emptyList()

            // Calculate correct average (only ratings > 0)
            val reviewsWithRatings = allReviews.filter { it.rating > 0.0 }
            val calculatedAverageRating = if (reviewsWithRatings.isNotEmpty()) {
                reviewsWithRatings.map { it.rating }.average()
            } else {
                0.0
            }

            // Count reviews with comments separately
            val reviewsWithComments = allReviews.filter { it.comment.isNotBlank() }
            val calculatedTotalReviews = reviewsWithComments.size
            val calculatedTotalRatings = reviewsWithRatings.size

            // ADDED: Count actual bookmarks from database
            val bookmarkCountResult = countRestaurantBookmarksUseCase(restaurant.id)
            val actualBookmarkCount = bookmarkCountResult.getOrNull() ?: 0

            _uiState.update {
                it.copy(
                    isLoading = false,
                    restaurant = restaurant,
                    stats = stats,
                    recentReviews = allReviews,
                    calculatedAverageRating = calculatedAverageRating,
                    calculatedTotalRatings = calculatedTotalRatings,
                    calculatedTotalReviews = calculatedTotalReviews,
                    actualBookmarkCount = actualBookmarkCount,  // ADDED
                    hasRestaurant = true,
                    error = null
                )
            }
        }
    }

    private fun mapErrorToMessage(exception: Throwable?): String {
        return when (exception) {
            is DomainException.NetworkError ->
                "Network error. Please check your connection"
            is DomainException.ResourceNotFound ->
                "No restaurant found for this owner"
            is DomainException.PermissionDenied ->
                "Permission denied. Please try logging in again"
            else ->
                "Failed to load restaurant data"
        }
    }
}