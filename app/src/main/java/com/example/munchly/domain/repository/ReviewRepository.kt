package com.example.munchly.domain.repositories

import com.example.munchly.domain.models.ReviewDomain

// ============================================================================
// REPOSITORY INTERFACE - Domain layer contract for review data access
// ============================================================================

/**
 * Repository interface defining data access operations for reviews.
 * This interface lives in the domain layer and is implemented by the data layer.
 */
interface ReviewRepository {

    /**
     * Creates a new review for a restaurant.
     * @return Created review with generated ID
     */
    suspend fun createReview(review: ReviewDomain): Result<ReviewDomain>

    /**
     * Retrieves reviews for a specific restaurant.
     * @param limit Optional limit for number of reviews (null = all)
     * @return List of reviews ordered by creation date (newest first)
     */
    suspend fun getReviewsByRestaurant(
        restaurantId: String,
        limit: Int? = null
    ): Result<List<ReviewDomain>>

    /**
     * Retrieves all reviews created by a user.
     * @return List of reviews ordered by creation date (newest first)
     */
    suspend fun getReviewsByUser(userId: String): Result<List<ReviewDomain>>

    /**
     * Retrieves a user's review for a specific restaurant.
     * @return Review if exists, null otherwise
     */
    suspend fun getUserReviewForRestaurant(
        userId: String,
        restaurantId: String
    ): Result<ReviewDomain?>

    /**
     * Updates an existing review.
     * @return Updated review
     */
    suspend fun updateReview(review: ReviewDomain): Result<ReviewDomain>

    /**
     * Deletes a review by its ID.
     */
    suspend fun deleteReview(reviewId: String): Result<Unit>
}