package com.example.munchly.domain.services

import com.example.munchly.domain.models.RestaurantDomain
import com.example.munchly.domain.models.RestaurantListItem
import com.example.munchly.domain.models.ReviewDomain

/**
 * Service responsible for search and filtering logic.
 * ENHANCED: Added helper method for calculating average ratings consistently.
 */
class SearchService {

    /**
     * Converts full restaurant domain model to lightweight list item.
     * Used for displaying search results efficiently.
     */
    fun toListItem(
        restaurant: RestaurantDomain,
        averageRating: Double = 0.0,
        totalReviews: Int = 0
    ): RestaurantListItem {
        return RestaurantListItem(
            id = restaurant.id,
            name = restaurant.name,
            tags = restaurant.tags,
            priceRange = restaurant.priceRange,
            averageRating = averageRating,
            totalReviews = totalReviews,
            images = restaurant.images
        )
    }

    /**
     * ADDED: Calculates average rating from a list of reviews.
     * Only includes ratings > 0 in the calculation.
     *
     * @param reviews List of reviews to calculate average from
     * @return Average rating (0.0 if no valid ratings exist)
     */
    fun calculateAverageRating(reviews: List<ReviewDomain>): Double {
        val validRatings = reviews.filter { it.rating > 0.0 }
        return if (validRatings.isNotEmpty()) {
            validRatings.map { it.rating }.average()
        } else {
            0.0
        }
    }

    /**
     * Filters restaurants by search query (name or tags).
     * Performs case-insensitive matching.
     */
    fun filterRestaurants(
        restaurants: List<RestaurantDomain>,
        query: String
    ): List<RestaurantDomain> {
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
     * Sorts restaurants by relevance to search query.
     * Prioritizes exact name matches, then tag matches.
     */
    fun sortByRelevance(
        restaurants: List<RestaurantDomain>,
        query: String
    ): List<RestaurantDomain> {
        if (query.isBlank()) {
            return restaurants
        }

        val normalizedQuery = query.lowercase().trim()

        return restaurants.sortedWith(
            compareByDescending<RestaurantDomain> { restaurant ->
                restaurant.name.lowercase() == normalizedQuery
            }.thenByDescending { restaurant ->
                restaurant.name.lowercase().startsWith(normalizedQuery)
            }.thenByDescending { restaurant ->
                restaurant.name.lowercase().contains(normalizedQuery)
            }.thenByDescending { restaurant ->
                restaurant.tags.any { it.lowercase() == normalizedQuery }
            }.thenByDescending { restaurant ->
                restaurant.tags.any { it.lowercase().startsWith(normalizedQuery) }
            }
        )
    }
}