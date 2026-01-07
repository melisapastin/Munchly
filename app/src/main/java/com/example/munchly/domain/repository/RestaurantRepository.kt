package com.example.munchly.domain.repositories

import com.example.munchly.domain.models.RestaurantDomain
import com.example.munchly.domain.models.RestaurantStatsDomain
import com.example.munchly.domain.models.ReviewDomain

// ============================================================================
// REPOSITORY INTERFACE - Domain layer contract for data access
// ============================================================================

/**
 * Repository interface defining data access operations for restaurants.
 * This interface lives in the domain layer and is implemented by the data layer.
 * This achieves Dependency Inversion: domain depends on abstraction,
 * data layer depends on the same abstraction.
 */
interface RestaurantRepository {

    /**
     * Retrieves a restaurant by its owner's ID.
     * @return Restaurant if found, null otherwise
     */
    suspend fun getRestaurantByOwnerId(ownerId: String): Result<RestaurantDomain?>

    /**
     * Retrieves statistics for a restaurant.
     * @return Statistics if found, null otherwise
     */
    suspend fun getRestaurantStats(restaurantId: String): Result<RestaurantStatsDomain?>

    /**
     * Retrieves recent reviews for a restaurant.
     * @param limit Maximum number of reviews to retrieve
     * @return List of reviews, empty if none found
     */
    suspend fun getRecentReviews(restaurantId: String, limit: Int): Result<List<ReviewDomain>>

    /**
     * Updates an existing restaurant.
     * @return Updated restaurant domain model
     */
    suspend fun updateRestaurant(restaurant: RestaurantDomain): Result<RestaurantDomain>

    /**
     * Creates a new restaurant.
     * @return Created restaurant domain model with generated ID
     */
    suspend fun createRestaurant(restaurant: RestaurantDomain): Result<RestaurantDomain>

    /**
     * Increments restaurant statistics after a review.
     */
    suspend fun incrementRestaurantStats(restaurantId: String, newRating: Double, hasComment: Boolean): Result<Unit>

    /**
     * Increments the view count for a restaurant.
     */
    suspend fun incrementRestaurantViews(restaurantId: String): Result<Unit>
}