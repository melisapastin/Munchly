package com.example.munchly.domain.usecases

import com.example.munchly.domain.exceptions.DomainException
import com.example.munchly.domain.models.RestaurantDomain
import com.example.munchly.domain.models.RestaurantInput
import com.example.munchly.domain.models.RestaurantStatsDomain
import com.example.munchly.domain.models.ReviewDomain
import com.example.munchly.domain.repositories.RestaurantRepository
import com.example.munchly.domain.services.RestaurantService

// ============================================================================
// USE CASES - Application-specific business operations
// ============================================================================

/**
 * Creates a new restaurant profile.
 * Validates input and delegates to repository for persistence.
 */
class CreateRestaurantUseCase(
    private val repository: RestaurantRepository,
    private val service: RestaurantService = RestaurantService()
) {
    suspend operator fun invoke(input: RestaurantInput): Result<RestaurantDomain> {
        // Validate input
        service.validateInput(input)?.let { error ->
            return Result.failure(DomainException.ValidationError(error))
        }

        // Create domain model
        val domainModel = service.createDomainModel(input)

        // Persist via repository
        return repository.createRestaurant(domainModel)
    }
}

/**
 * Updates an existing restaurant profile.
 * Validates input and preserves creation timestamp.
 */
class UpdateRestaurantUseCase(
    private val repository: RestaurantRepository,
    private val service: RestaurantService = RestaurantService()
) {
    suspend operator fun invoke(
        restaurantId: String,
        input: RestaurantInput,
        createdAt: Long
    ): Result<RestaurantDomain> {
        // Validate input
        service.validateInput(input)?.let { error ->
            return Result.failure(DomainException.ValidationError(error))
        }

        // Create domain model with existing ID and creation timestamp
        val domainModel = service.createDomainModel(
            input = input,
            id = restaurantId,
            createdAt = createdAt
        )

        // Update via repository
        return repository.updateRestaurant(domainModel)
    }
}

/**
 * Retrieves a restaurant by its owner's ID.
 * Returns null if no restaurant exists for the owner.
 */
class GetRestaurantByOwnerIdUseCase(
    private val repository: RestaurantRepository
) {
    suspend operator fun invoke(ownerId: String): Result<RestaurantDomain?> {
        if (ownerId.isBlank()) {
            return Result.failure(
                DomainException.ValidationError("Owner ID cannot be empty")
            )
        }
        return repository.getRestaurantByOwnerId(ownerId)
    }
}

/**
 * Retrieves statistics for a restaurant.
 * Returns default stats if none exist (business rule: always show stats).
 */
class GetRestaurantStatsUseCase(
    private val repository: RestaurantRepository
) {
    suspend operator fun invoke(restaurantId: String): Result<RestaurantStatsDomain> {
        return repository.getRestaurantStats(restaurantId)
            .map { stats ->
                // Business rule: Return default stats if none exist
                stats ?: createDefaultStats(restaurantId)
            }
    }

    private fun createDefaultStats(restaurantId: String) = RestaurantStatsDomain(
        restaurantId = restaurantId,
        totalReviews = 0,
        totalRatings = 0,
        averageRating = 0.0,
        totalBookmarks = 0,
        monthlyViews = 0,
        lastUpdated = System.currentTimeMillis()
    )
}

/**
 * Retrieves recent reviews for a restaurant.
 * Default limit is 5 reviews (business rule).
 */
class GetRecentReviewsUseCase(
    private val repository: RestaurantRepository
) {
    suspend operator fun invoke(
        restaurantId: String,
        limit: Int = DEFAULT_REVIEW_LIMIT
    ): Result<List<ReviewDomain>> {
        return repository.getRecentReviews(restaurantId, limit)
    }

    companion object {
        private const val DEFAULT_REVIEW_LIMIT = 5
    }
}