package com.example.munchly.domain.repositories

import com.example.munchly.domain.models.RestaurantDomain

// ============================================================================
// REPOSITORY INTERFACE - Domain layer contract for restaurant search
// ============================================================================

/**
 * Repository interface defining data access operations for restaurant discovery.
 * This interface lives in the domain layer and is implemented by the data layer.
 */
interface RestaurantSearchRepository {

    /**
     * Retrieves all restaurants.
     * @return List of all restaurants
     */
    suspend fun getAllRestaurants(): Result<List<RestaurantDomain>>

    /**
     * Searches restaurants by name (case-insensitive partial match).
     * @param query Search query string
     * @return List of matching restaurants
     */
    suspend fun searchRestaurantsByName(query: String): Result<List<RestaurantDomain>>

    /**
     * Searches restaurants by tag (exact match).
     * @param tag Tag to search for
     * @return List of restaurants with the specified tag
     */
    suspend fun searchRestaurantsByTag(tag: String): Result<List<RestaurantDomain>>

    /**
     * Retrieves a specific restaurant by ID.
     * @return Restaurant if found, null otherwise
     */
    suspend fun getRestaurantById(restaurantId: String): Result<RestaurantDomain?>
}