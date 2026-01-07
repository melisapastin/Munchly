package com.example.munchly.domain.usecases

import com.example.munchly.domain.exceptions.DomainException
import com.example.munchly.domain.models.RestaurantDomain
import com.example.munchly.domain.repositories.RestaurantSearchRepository
import com.example.munchly.domain.repositories.RestaurantRepository
import com.example.munchly.domain.services.SearchService

// ============================================================================
// DATA CLASSES
// ============================================================================

/**
 * Container for restaurant with statistics.
 */
data class RestaurantDetails(
    val restaurant: RestaurantDomain,
    val averageRating: Double,
    val totalReviews: Int,
    val totalRatings: Int
)

// ============================================================================
// RESTAURANT SEARCH USE CASES - Application-specific search operations
// ============================================================================

/**
 * Searches restaurants by name or tags.
 * If query is empty, returns all restaurants.
 */
class SearchRestaurantsUseCase(
    private val repository: RestaurantSearchRepository,
    private val searchService: SearchService = SearchService()
) {
    suspend operator fun invoke(query: String): Result<List<RestaurantDomain>> {
        val trimmedQuery = query.trim()

        // If no query, return all restaurants
        if (trimmedQuery.isEmpty()) {
            return repository.getAllRestaurants()
        }

        // Search by name (includes client-side filtering by tags)
        val searchResult = repository.searchRestaurantsByName(trimmedQuery)

        if (searchResult.isFailure) {
            return searchResult
        }

        val restaurants = searchResult.getOrNull() ?: emptyList()

        // Sort by relevance
        val sorted = searchService.sortByRelevance(restaurants, trimmedQuery)

        return Result.success(sorted)
    }
}

/**
 * Gets all restaurants for the feed.
 * Returns restaurants ordered by creation date (newest first).
 */
class GetAllRestaurantsUseCase(
    private val repository: RestaurantSearchRepository
) {
    suspend operator fun invoke(): Result<List<RestaurantDomain>> {
        return repository.getAllRestaurants()
    }
}

/**
 * Gets a specific restaurant by ID with full details.
 */
class GetRestaurantDetailsUseCase(
    private val searchRepository: RestaurantSearchRepository,
    private val restaurantRepository: RestaurantRepository
) {
    suspend operator fun invoke(restaurantId: String): Result<RestaurantDetails> {
        if (restaurantId.isBlank()) {
            return Result.failure(
                DomainException.ValidationError("Restaurant ID is required")
            )
        }

        // Get restaurant details
        val restaurantResult = searchRepository.getRestaurantById(restaurantId)
        if (restaurantResult.isFailure) {
            return Result.failure(restaurantResult.exceptionOrNull()!!)
        }

        val restaurant = restaurantResult.getOrNull()
        if (restaurant == null) {
            return Result.failure(
                DomainException.ResourceNotFound("Restaurant", null)
            )
        }

        // Get restaurant stats
        val statsResult = restaurantRepository.getRestaurantStats(restaurantId)
        val stats = statsResult.getOrNull()

        return Result.success(
            RestaurantDetails(
                restaurant = restaurant,
                averageRating = stats?.averageRating ?: 0.0,
                totalReviews = stats?.totalReviews ?: 0,
                totalRatings = stats?.totalRatings ?: 0
            )
        )
    }
}