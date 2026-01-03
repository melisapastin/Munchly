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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ============================================================================
// STATE
// ============================================================================

/**
 * UI state for restaurant owner feed/dashboard.
 * Uses domain models exclusively.
 */
data class OwnerFeedState(
    val isLoading: Boolean = true,
    val restaurant: RestaurantDomain? = null,
    val stats: RestaurantStatsDomain? = null,
    val recentReviews: List<ReviewDomain> = emptyList(),
    val error: String? = null,
    val hasRestaurant: Boolean = false
)

// ============================================================================
// VIEWMODEL
// ============================================================================

/**
 * ViewModel for restaurant owner dashboard.
 * Loads restaurant data, statistics, and recent reviews.
 */
class OwnerFeedViewModel(
    private val ownerId: String,
    private val getRestaurantByOwnerUseCase: GetRestaurantByOwnerIdUseCase,
    private val getRestaurantStatsUseCase: GetRestaurantStatsUseCase,
    private val getRecentReviewsUseCase: GetRecentReviewsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OwnerFeedState())
    val uiState: StateFlow<OwnerFeedState> = _uiState.asStateFlow()

    init {
        loadRestaurantData()
    }

    /**
     * Loads all restaurant data including stats and reviews.
     * Public method to allow manual refresh.
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

            // Load stats and reviews in parallel
            val statsResult = getRestaurantStatsUseCase(restaurant.id)
            val reviewsResult = getRecentReviewsUseCase(restaurant.id, limit = 5)

            // Note: We don't fail the entire screen if stats/reviews fail
            // These are supplementary data - show what we can
            val stats = statsResult.getOrNull()
            val reviews = reviewsResult.getOrNull() ?: emptyList()

            _uiState.update {
                it.copy(
                    isLoading = false,
                    restaurant = restaurant,
                    stats = stats,
                    recentReviews = reviews,
                    hasRestaurant = true,
                    error = null
                )
            }
        }
    }

    /**
     * Maps domain exceptions to user-friendly error messages.
     */
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