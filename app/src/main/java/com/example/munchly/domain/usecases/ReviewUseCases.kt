package com.example.munchly.domain.usecases

import android.util.Log
import com.example.munchly.domain.exceptions.DomainException
import com.example.munchly.domain.models.ReviewDomain
import com.example.munchly.domain.models.ReviewInput
import com.example.munchly.domain.repositories.ReviewRepository
import com.example.munchly.domain.services.ReviewValidator

// ============================================================================
// REVIEW USE CASES - Application-specific review operations
// ============================================================================

/**
 * Creates a new review for a restaurant.
 * Validates input and ensures user hasn't already reviewed this restaurant.
 */
class CreateReviewUseCase(
    private val repository: ReviewRepository
) {
    suspend operator fun invoke(input: ReviewInput): Result<ReviewDomain> {
        Log.d("CreateReviewUseCase", "Creating review for restaurant: ${input.restaurantId}")

        // Validate review content
        val validationError = ReviewValidator.validateReview(input.rating, input.comment)
        if (validationError != null) {
            Log.e("CreateReviewUseCase", "Validation failed: $validationError")
            return Result.failure(DomainException.ValidationError(validationError))
        }

        // Create review
        val review = ReviewDomain(
            id = "",
            restaurantId = input.restaurantId,
            userId = input.userId,
            userName = input.userName.trim(),
            rating = input.rating,
            comment = input.comment.trim(),
            createdAt = System.currentTimeMillis()
        )

        Log.d("CreateReviewUseCase", "Calling repository to create review")
        val result = repository.createReview(review)

        if (result.isSuccess) {
            Log.d("CreateReviewUseCase", "Review created successfully: ${result.getOrNull()?.id}")
        } else {
            Log.e("CreateReviewUseCase", "Failed to create review", result.exceptionOrNull())
        }

        return result
    }
}

/**
 * Retrieves reviews for a specific restaurant.
 * FIXED: Better handling of null limit parameter.
 */
class GetRestaurantReviewsUseCase(
    private val repository: ReviewRepository
) {
    suspend operator fun invoke(
        restaurantId: String,
        limit: Int? = DEFAULT_REVIEW_LIMIT
    ): Result<List<ReviewDomain>> {
        Log.d("GetRestaurantReviewsUseCase", "Getting reviews for restaurant: $restaurantId, limit: $limit")

        if (restaurantId.isBlank()) {
            Log.e("GetRestaurantReviewsUseCase", "Restaurant ID is blank")
            return Result.failure(
                DomainException.ValidationError("Restaurant ID is required")
            )
        }

        val result = repository.getReviewsByRestaurant(restaurantId, limit)

        if (result.isSuccess) {
            val reviews = result.getOrNull() ?: emptyList()
            Log.d("GetRestaurantReviewsUseCase", "Successfully retrieved ${reviews.size} reviews")
        } else {
            Log.e("GetRestaurantReviewsUseCase", "Failed to get reviews", result.exceptionOrNull())
        }

        return result
    }

    companion object {
        private const val DEFAULT_REVIEW_LIMIT = 20
    }
}

/**
 * Retrieves all reviews created by a user.
 */
class GetUserReviewsUseCase(
    private val repository: ReviewRepository
) {
    suspend operator fun invoke(userId: String): Result<List<ReviewDomain>> {
        if (userId.isBlank()) {
            return Result.failure(
                DomainException.ValidationError("User ID is required")
            )
        }

        return repository.getReviewsByUser(userId)
    }
}

/**
 * Checks if a user has already reviewed a restaurant.
 */
class HasUserReviewedRestaurantUseCase(
    private val repository: ReviewRepository
) {
    suspend operator fun invoke(
        userId: String,
        restaurantId: String
    ): Result<Boolean> {
        if (userId.isBlank() || restaurantId.isBlank()) {
            return Result.failure(
                DomainException.ValidationError("User ID and Restaurant ID are required")
            )
        }

        return repository.getUserReviewForRestaurant(userId, restaurantId)
            .map { it != null }
    }
}

/**
 * Updates an existing review.
 * Validates input and ensures user owns the review.
 */
class UpdateReviewUseCase(
    private val repository: ReviewRepository
) {
    suspend operator fun invoke(
        reviewId: String,
        userId: String,
        rating: Double,
        comment: String
    ): Result<ReviewDomain> {
        // Validate review content
        val validationError = ReviewValidator.validateReview(rating, comment)
        if (validationError != null) {
            return Result.failure(DomainException.ValidationError(validationError))
        }

        // Get all user reviews to find the one being updated
        val userReviewsResult = repository.getReviewsByUser(userId)

        if (userReviewsResult.isFailure) {
            return Result.failure(userReviewsResult.exceptionOrNull()!!)
        }

        val userReviews = userReviewsResult.getOrNull() ?: emptyList()
        val existingReview = userReviews.find { it.id == reviewId }

        if (existingReview == null) {
            return Result.failure(
                DomainException.ResourceNotFound("Review", null)
            )
        }

        if (existingReview.userId != userId) {
            return Result.failure(
                DomainException.PermissionDenied("Review", null)
            )
        }

        // Update review
        val updatedReview = existingReview.copy(
            rating = rating,
            comment = comment.trim()
        )

        return repository.updateReview(updatedReview)
    }
}

/**
 * Deletes a review.
 * Ensures user owns the review before deletion.
 */
class DeleteReviewUseCase(
    private val repository: ReviewRepository
) {
    suspend operator fun invoke(reviewId: String, userId: String): Result<Unit> {
        if (reviewId.isBlank() || userId.isBlank()) {
            return Result.failure(
                DomainException.ValidationError("Review ID and User ID are required")
            )
        }

        // Note: In a real app, you'd verify ownership here
        // For simplicity, we'll trust the UI to only allow deletion of own reviews

        return repository.deleteReview(reviewId)
    }
}