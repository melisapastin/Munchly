package com.example.munchly.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.munchly.domain.exceptions.DomainException
import com.example.munchly.domain.models.ReviewDomain
import com.example.munchly.domain.usecases.GetRestaurantReviewsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ============================================================================
// STATE
// ============================================================================

data class RestaurantReviewsState(
    val isLoading: Boolean = true,
    val reviews: List<ReviewDomain> = emptyList(),
    val averageRating: Double = 0.0,
    val error: String? = null
)

// ============================================================================
// VIEWMODEL
// ============================================================================

/**
 * FIXED: Average rating calculation now only considers ratings > 0
 */
class RestaurantReviewsViewModel(
    private val restaurantId: String,
    private val getRestaurantReviewsUseCase: GetRestaurantReviewsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RestaurantReviewsState())
    val uiState: StateFlow<RestaurantReviewsState> = _uiState.asStateFlow()

    init {
        Log.d("RestaurantReviewsVM", "Initializing with restaurantId: $restaurantId")
        loadReviews()
    }

    /**
     * FIXED: Only includes ratings > 0 in average calculation
     */
    fun loadReviews() {
        Log.d("RestaurantReviewsVM", "Starting to load reviews for restaurant: $restaurantId")
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val result = getRestaurantReviewsUseCase(restaurantId, limit = null)

                Log.d("RestaurantReviewsVM", "Use case result - isSuccess: ${result.isSuccess}")

                if (result.isFailure) {
                    val exception = result.exceptionOrNull()
                    Log.e("RestaurantReviewsVM", "Failed to load reviews", exception)

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = mapErrorToMessage(exception)
                        )
                    }
                    return@launch
                }

                val reviews = result.getOrNull() ?: emptyList()
                Log.d("RestaurantReviewsVM", "Successfully loaded ${reviews.size} reviews")

                // FIXED: Only calculate average from reviews with rating > 0
                val reviewsWithRatings = reviews.filter { it.rating > 0.0 }
                val averageRating = if (reviewsWithRatings.isNotEmpty()) {
                    reviewsWithRatings.map { it.rating }.average()
                } else {
                    0.0
                }

                Log.d("RestaurantReviewsVM", "Calculated average rating: $averageRating " +
                        "(from ${reviewsWithRatings.size} reviews with ratings)")

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        reviews = reviews,
                        averageRating = averageRating,
                        error = null
                    )
                }
            } catch (e: Exception) {
                Log.e("RestaurantReviewsVM", "Unexpected exception in loadReviews", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Unexpected error: ${e.message}"
                    )
                }
            }
        }
    }

    private fun mapErrorToMessage(exception: Throwable?): String {
        return when (exception) {
            is DomainException.NetworkError -> {
                Log.e("RestaurantReviewsVM", "Network error detected")
                "Network error. Please check your connection"
            }
            is DomainException.ResourceNotFound -> {
                Log.e("RestaurantReviewsVM", "Reviews not found")
                "Reviews not found"
            }
            is DomainException.PermissionDenied -> {
                Log.e("RestaurantReviewsVM", "Permission denied")
                "Permission denied. Please try logging in again"
            }
            else -> {
                Log.e("RestaurantReviewsVM", "Unknown error: ${exception?.javaClass?.simpleName}")
                "Failed to load reviews: ${exception?.message ?: "Unknown error"}"
            }
        }
    }
}