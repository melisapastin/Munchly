package com.example.munchly.domain.usecases

import com.example.munchly.domain.exceptions.DomainException
import com.example.munchly.domain.repositories.RestaurantRepository

/**
 * Increments restaurant statistics after a review/rating.
 * Updates total ratings, reviews, and average rating atomically.
 */
class IncrementRestaurantStatsUseCase(
    private val repository: RestaurantRepository
) {
    suspend operator fun invoke(
        restaurantId: String,
        newRating: Double,
        hasComment: Boolean
    ): Result<Unit> {
        if (restaurantId.isBlank()) {
            return Result.failure(
                DomainException.ValidationError("Restaurant ID is required")
            )
        }

        return repository.incrementRestaurantStats(restaurantId, newRating, hasComment)
    }
}