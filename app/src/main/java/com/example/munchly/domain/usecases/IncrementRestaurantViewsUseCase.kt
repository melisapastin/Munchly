package com.example.munchly.domain.usecases

import com.example.munchly.domain.exceptions.DomainException
import com.example.munchly.domain.repositories.RestaurantRepository

/**
 * Increments the view count for a restaurant.
 * Called whenever a food lover views restaurant details.
 */
class IncrementRestaurantViewsUseCase(
    private val repository: RestaurantRepository
) {
    suspend operator fun invoke(restaurantId: String): Result<Unit> {
        if (restaurantId.isBlank()) {
            return Result.failure(
                DomainException.ValidationError("Restaurant ID is required")
            )
        }

        return repository.incrementRestaurantViews(restaurantId)
    }
}