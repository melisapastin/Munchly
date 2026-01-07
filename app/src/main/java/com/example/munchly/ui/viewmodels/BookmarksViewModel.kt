package com.example.munchly.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.munchly.domain.exceptions.DomainException
import com.example.munchly.domain.models.RestaurantListItem
import com.example.munchly.domain.usecases.GetRestaurantDetailsUseCase
import com.example.munchly.domain.usecases.GetRestaurantReviewsUseCase
import com.example.munchly.domain.usecases.GetUserBookmarksUseCase
import com.example.munchly.domain.usecases.ToggleBookmarkUseCase
import com.example.munchly.domain.services.SearchService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ============================================================================
// STATE
// ============================================================================

data class BookmarksState(
    val isLoading: Boolean = true,
    val bookmarkedRestaurants: List<RestaurantListItem> = emptyList(),
    val error: String? = null
)

// ============================================================================
// VIEWMODEL - FIXED
// ============================================================================

/**
 * FIXED: Now calculates average rating from reviews instead of using stats
 */
class BookmarksViewModel(
    private val userId: String,
    private val getUserBookmarksUseCase: GetUserBookmarksUseCase,
    private val getRestaurantDetailsUseCase: GetRestaurantDetailsUseCase,
    private val getRestaurantReviewsUseCase: GetRestaurantReviewsUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase,
    private val searchService: SearchService = SearchService()
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookmarksState())
    val uiState: StateFlow<BookmarksState> = _uiState.asStateFlow()

    init {
        loadBookmarks()
    }

    /**
     * FIXED: Calculates average rating from reviews for each restaurant
     */
    fun loadBookmarks() {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                Log.d("BookmarksViewModel", "Loading bookmarks for user: $userId")

                val bookmarksResult = getUserBookmarksUseCase(userId)

                if (bookmarksResult.isFailure) {
                    val exception = bookmarksResult.exceptionOrNull()
                    Log.e("BookmarksViewModel", "Failed to load bookmarks", exception)

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = mapErrorToMessage(exception)
                        )
                    }
                    return@launch
                }

                val bookmarks = bookmarksResult.getOrNull() ?: emptyList()
                Log.d("BookmarksViewModel", "Found ${bookmarks.size} bookmarks")

                if (bookmarks.isEmpty()) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            bookmarkedRestaurants = emptyList(),
                            error = null
                        )
                    }
                    return@launch
                }

                val restaurants = mutableListOf<RestaurantListItem>()

                bookmarks.forEachIndexed { index, bookmark ->
                    try {
                        Log.d("BookmarksViewModel", "Loading details for restaurant ${index + 1}/${bookmarks.size}: ${bookmark.restaurantId}")

                        val detailsResult = getRestaurantDetailsUseCase(bookmark.restaurantId)

                        if (detailsResult.isSuccess) {
                            val details = detailsResult.getOrNull()

                            if (details != null) {
                                // FIXED: Load reviews and calculate correct average
                                val reviewsResult = getRestaurantReviewsUseCase(bookmark.restaurantId, limit = null)
                                val reviews = reviewsResult.getOrNull() ?: emptyList()

                                // FIXED: Only count ratings > 0
                                val reviewsWithRatings = reviews.filter { it.rating > 0.0 }
                                val averageRating = if (reviewsWithRatings.isNotEmpty()) {
                                    reviewsWithRatings.map { it.rating }.average()
                                } else {
                                    0.0
                                }

                                val reviewsWithComments = reviews.filter { it.comment.isNotBlank() }
                                val totalReviews = reviewsWithComments.size

                                val listItem = searchService.toListItem(
                                    restaurant = details.restaurant,
                                    averageRating = averageRating,
                                    totalReviews = totalReviews
                                )
                                restaurants.add(listItem)
                                Log.d("BookmarksViewModel", "Successfully loaded: ${details.restaurant.name} " +
                                        "with calculated avg: $averageRating (from ${reviewsWithRatings.size} ratings)")
                            } else {
                                Log.w("BookmarksViewModel", "Details were null for ${bookmark.restaurantId}")
                            }
                        } else {
                            Log.e("BookmarksViewModel", "Failed to load details for ${bookmark.restaurantId}", detailsResult.exceptionOrNull())
                        }
                    } catch (e: Exception) {
                        Log.e("BookmarksViewModel", "Exception loading restaurant ${bookmark.restaurantId}", e)
                    }
                }

                Log.d("BookmarksViewModel", "Successfully loaded ${restaurants.size} restaurant details")

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        bookmarkedRestaurants = restaurants,
                        error = if (restaurants.isEmpty() && bookmarks.isNotEmpty()) {
                            "Could not load restaurant details"
                        } else {
                            null
                        }
                    )
                }
            } catch (e: Exception) {
                Log.e("BookmarksViewModel", "Unexpected error in loadBookmarks", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "An unexpected error occurred: ${e.message}"
                    )
                }
            }
        }
    }

    fun removeBookmark(restaurantId: String) {
        viewModelScope.launch {
            val result = toggleBookmarkUseCase(userId, restaurantId)

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        bookmarkedRestaurants = it.bookmarkedRestaurants.filter { restaurant ->
                            restaurant.id != restaurantId
                        }
                    )
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
            is DomainException.ResourceNotFound ->
                "Could not find bookmarked restaurants"
            else ->
                "Failed to load bookmarks: ${exception?.message ?: "Unknown error"}"
        }
    }
}