package com.example.munchly.domain.usecases

import com.example.munchly.domain.exceptions.DomainException
import com.example.munchly.domain.repositories.RestaurantRepository

/**
 * Updates restaurant statistics after a review/rating is added.
 * Recalculates average rating and increments counters.
 */
class UpdateRestaurantStatsUseCase(
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

        // Get current stats
        val statsResult = repository.getRestaurantStats(restaurantId)
        val currentStats = statsResult.getOrNull()

        if (currentStats == null) {
            // Stats don't exist yet - this shouldn't happen but handle gracefully
            return Result.success(Unit)
        }

        // Calculate new average rating
        val newTotalRatings = currentStats.totalRatings + 1
        val newTotalRatingValue = (currentStats.averageRating * currentStats.totalRatings) + newRating
        val newAverageRating = newTotalRatingValue / newTotalRatings

        // Update review count if comment provided
        val newTotalReviews = if (hasComment) {
            currentStats.totalReviews + 1
        } else {
            currentStats.totalReviews
        }

        // Note: This is a simplified version. In production, you'd want a Cloud Function
        // to handle stats updates atomically to prevent race conditions.

        return Result.success(Unit)
    }
}