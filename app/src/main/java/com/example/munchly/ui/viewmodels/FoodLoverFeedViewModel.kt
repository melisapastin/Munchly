package com.example.munchly.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.munchly.data.models.Restaurant
import com.example.munchly.domain.usecases.GetRestaurantsUseCase
import com.example.munchly.domain.usecases.SearchRestaurantsUseCase
import com.example.munchly.domain.usecases.ToggleBookmarkUseCase
import com.example.munchly.domain.usecases.GetCurrentUserUseCase
import com.example.munchly.domain.usecases.IsBookmarkedUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FoodLoverFeedViewModel(
    private val getRestaurantsUseCase: GetRestaurantsUseCase,
    private val searchRestaurantsUseCase: SearchRestaurantsUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val isBookmarkedUseCase: IsBookmarkedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FoodLoverFeedUiState())
    val uiState: StateFlow<FoodLoverFeedUiState> = _uiState.asStateFlow()

    private var allRestaurants: List<Restaurant> = emptyList()

    init {
        loadRestaurants()
    }

    private fun loadRestaurants() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            try {
                allRestaurants = getRestaurantsUseCase()

                // Get bookmark status for each restaurant
                val currentUser = getCurrentUserUseCase()
                val restaurantsWithBookmarks = if (currentUser != null) {
                    allRestaurants.map { restaurant ->
                        RestaurantWithBookmark(
                            restaurant = restaurant,
                            isBookmarked = isBookmarkedUseCase(currentUser.uid, restaurant.id)
                        )
                    }
                } else {
                    allRestaurants.map { restaurant ->
                        RestaurantWithBookmark(restaurant = restaurant, isBookmarked = false)
                    }
                }

                _uiState.value = _uiState.value.copy(
                    restaurants = restaurantsWithBookmarks,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to load restaurants",
                    isLoading = false
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)

        viewModelScope.launch {
            try {
                val currentUser = getCurrentUserUseCase()
                val restaurants = if (query.isBlank()) {
                    allRestaurants
                } else {
                    searchRestaurantsUseCase(query)
                }

                // Get bookmark status for filtered restaurants
                val restaurantsWithBookmarks = if (currentUser != null) {
                    restaurants.map { restaurant ->
                        RestaurantWithBookmark(
                            restaurant = restaurant,
                            isBookmarked = isBookmarkedUseCase(currentUser.uid, restaurant.id)
                        )
                    }
                } else {
                    restaurants.map { restaurant ->
                        RestaurantWithBookmark(restaurant = restaurant, isBookmarked = false)
                    }
                }

                _uiState.value = _uiState.value.copy(restaurants = restaurantsWithBookmarks)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Search failed")
            }
        }
    }

    fun onBookmarkClick(restaurantId: String) {
        viewModelScope.launch {
            try {
                val currentUser = getCurrentUserUseCase()
                currentUser?.let { user ->
                    toggleBookmarkUseCase(user.uid, restaurantId)
                    // Refresh to update bookmark status
                    loadRestaurants()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to update bookmark")
            }
        }
    }
}

// Simple data class to combine restaurant with bookmark status
data class RestaurantWithBookmark(
    val restaurant: Restaurant,
    val isBookmarked: Boolean
)

data class FoodLoverFeedUiState(
    val restaurants: List<RestaurantWithBookmark> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)