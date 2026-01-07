package com.example.munchly.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.munchly.domain.exceptions.DomainException
import com.example.munchly.domain.models.RestaurantListItem
import com.example.munchly.domain.usecases.GetAllRestaurantsUseCase
import com.example.munchly.domain.usecases.GetRestaurantReviewsUseCase
import com.example.munchly.domain.usecases.GetUserBookmarksUseCase
import com.example.munchly.domain.usecases.SearchRestaurantsUseCase
import com.example.munchly.domain.usecases.ToggleBookmarkUseCase
import com.example.munchly.domain.services.SearchService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.munchly.domain.usecases.UpdateUserStatsUseCase

// ============================================================================
// STATE
// ============================================================================

data class FoodLoverFeedState(
    val isLoading: Boolean = true,
    val restaurants: List<RestaurantListItem> = emptyList(),
    val filteredRestaurants: List<RestaurantListItem> = emptyList(),
    val bookmarkedRestaurantIds: Set<String> = emptySet(),
    val searchQuery: String = "",
    val error: String? = null
)

// ============================================================================
// VIEWMODEL - FIXED BOOKMARK TRACKING
// ============================================================================

/**
 * FIXED: Now updates uniqueBookmarkedRestaurants Set correctly
 */
class FoodLoverFeedViewModel(
    private val userId: String,
    private val getAllRestaurantsUseCase: GetAllRestaurantsUseCase,
    private val getRestaurantReviewsUseCase: GetRestaurantReviewsUseCase,
    private val searchRestaurantsUseCase: SearchRestaurantsUseCase,
    private val getUserBookmarksUseCase: GetUserBookmarksUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase,
    private val updateUserStatsUseCase: UpdateUserStatsUseCase,
    private val searchService: SearchService = SearchService()
) : ViewModel() {

    private val _uiState = MutableStateFlow(FoodLoverFeedState())
    val uiState: StateFlow<FoodLoverFeedState> = _uiState.asStateFlow()

    init {
        loadRestaurants()
        loadBookmarks()
    }

    fun loadRestaurants() {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val result = getAllRestaurantsUseCase()

            if (result.isFailure) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = mapErrorToMessage(result.exceptionOrNull())
                    )
                }
                return@launch
            }

            val restaurants = result.getOrNull() ?: emptyList()

            val listItems = restaurants.map { restaurant ->
                val reviewsResult = getRestaurantReviewsUseCase(restaurant.id, limit = null)
                val reviews = reviewsResult.getOrNull() ?: emptyList()

                val reviewsWithRatings = reviews.filter { it.rating > 0.0 }
                val averageRating = if (reviewsWithRatings.isNotEmpty()) {
                    reviewsWithRatings.map { it.rating }.average()
                } else {
                    0.0
                }

                val reviewsWithComments = reviews.filter { it.comment.isNotBlank() }
                val totalReviews = reviewsWithComments.size

                searchService.toListItem(
                    restaurant = restaurant,
                    averageRating = averageRating,
                    totalReviews = totalReviews
                )
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    restaurants = listItems,
                    filteredRestaurants = filterRestaurants(listItems, it.searchQuery),
                    error = null
                )
            }
        }
    }

    private fun loadBookmarks() {
        viewModelScope.launch {
            val result = getUserBookmarksUseCase(userId)

            if (result.isSuccess) {
                val bookmarks = result.getOrNull() ?: emptyList()
                val bookmarkedIds = bookmarks.map { it.restaurantId }.toSet()

                _uiState.update { it.copy(bookmarkedRestaurantIds = bookmarkedIds) }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update {
            it.copy(
                searchQuery = query,
                filteredRestaurants = filterRestaurants(it.restaurants, query)
            )
        }
    }

    private fun filterRestaurants(
        restaurants: List<RestaurantListItem>,
        query: String
    ): List<RestaurantListItem> {
        if (query.isBlank()) {
            return restaurants
        }

        val normalizedQuery = query.lowercase().trim()

        return restaurants.filter { restaurant ->
            restaurant.name.lowercase().contains(normalizedQuery) ||
                    restaurant.tags.any { tag ->
                        tag.lowercase().contains(normalizedQuery)
                    }
        }
    }

    /**
     * FIXED: Now updates uniqueBookmarkedRestaurants Set correctly
     */
    fun toggleBookmark(restaurantId: String) {
        viewModelScope.launch {
            val result = toggleBookmarkUseCase(userId, restaurantId)

            if (result.isSuccess) {
                val isBookmarked = result.getOrNull() ?: false

                _uiState.update {
                    val updatedBookmarks = if (isBookmarked) {
                        it.bookmarkedRestaurantIds + restaurantId
                    } else {
                        it.bookmarkedRestaurantIds - restaurantId
                    }
                    it.copy(bookmarkedRestaurantIds = updatedBookmarks)
                }

                // Update user stats with Set tracking
                updateUserStatsUseCase(userId) { stats ->
                    if (isBookmarked) {
                        // Add to bookmarked set
                        stats.copy(
                            uniqueBookmarkedRestaurants = stats.uniqueBookmarkedRestaurants + restaurantId,
                            totalBookmarks = stats.uniqueBookmarkedRestaurants.size + 1
                        )
                    } else {
                        // Remove from bookmarked set
                        stats.copy(
                            uniqueBookmarkedRestaurants = stats.uniqueBookmarkedRestaurants - restaurantId,
                            totalBookmarks = (stats.uniqueBookmarkedRestaurants.size - 1).coerceAtLeast(0)
                        )
                    }
                }
            }
        }
    }

    private fun mapErrorToMessage(exception: Throwable?): String {
        return when (exception) {
            is DomainException.NetworkError ->
                "Network error. Please check your connection"
            is DomainException.PermissionDenied ->
                "Permission denied. Please try logging in again"
            else ->
                "Failed to load restaurants"
        }
    }
}