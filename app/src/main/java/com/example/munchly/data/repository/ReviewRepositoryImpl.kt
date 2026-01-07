package com.example.munchly.data.repository

import com.example.munchly.data.mappers.toData
import com.example.munchly.data.mappers.toDomain
import com.example.munchly.data.remote.ReviewRemoteDataSource
import com.example.munchly.domain.exceptions.DomainException
import com.example.munchly.domain.models.ReviewDomain
import com.example.munchly.domain.repositories.ReviewRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FirebaseFirestoreException

// ============================================================================
// REPOSITORY IMPLEMENTATION
// ============================================================================

/**
 * Implementation of ReviewRepository that uses Firebase as data source.
 * Handles exception mapping and data model conversion for food lover reviews.
 */
class ReviewRepositoryImpl(
    private val remoteDataSource: ReviewRemoteDataSource
) : ReviewRepository {

    override suspend fun createReview(review: ReviewDomain): Result<ReviewDomain> =
        safeCall(resourceName = "Review") {
            remoteDataSource.createReview(review.toData()).toDomain()
        }

    override suspend fun getReviewsByRestaurant(
        restaurantId: String,
        limit: Int?
    ): Result<List<ReviewDomain>> =
        safeCall(resourceName = "Reviews") {
            remoteDataSource.getReviewsByRestaurant(restaurantId, limit)
                .map { it.toDomain() }
        }

    override suspend fun getReviewsByUser(userId: String): Result<List<ReviewDomain>> =
        safeCall(resourceName = "Reviews") {
            remoteDataSource.getReviewsByUser(userId).map { it.toDomain() }
        }

    override suspend fun getUserReviewForRestaurant(
        userId: String,
        restaurantId: String
    ): Result<ReviewDomain?> =
        safeCall(resourceName = "Review") {
            remoteDataSource.getUserReviewForRestaurant(userId, restaurantId)?.toDomain()
        }

    override suspend fun updateReview(review: ReviewDomain): Result<ReviewDomain> =
        safeCall(resourceName = "Review") {
            remoteDataSource.updateReview(review.toData()).toDomain()
        }

    override suspend fun deleteReview(reviewId: String): Result<Unit> =
        safeCall(resourceName = "Review") {
            remoteDataSource.deleteReview(reviewId)
        }

    // ========================================================================
    // EXCEPTION MAPPING
    // ========================================================================

    private suspend fun <T> safeCall(
        resourceName: String = "Resource",
        block: suspend () -> T
    ): Result<T> {
        return try {
            Result.success(block())
        } catch (e: FirebaseNetworkException) {
            Result.failure(
                DomainException.NetworkError(originalCause = e)
            )
        } catch (e: FirebaseFirestoreException) {
            when (e.code) {
                FirebaseFirestoreException.Code.PERMISSION_DENIED ->
                    Result.failure(
                        DomainException.PermissionDenied(
                            resource = resourceName,
                            originalCause = e
                        )
                    )

                FirebaseFirestoreException.Code.NOT_FOUND ->
                    Result.failure(
                        DomainException.ResourceNotFound(
                            resource = resourceName,
                            originalCause = e
                        )
                    )

                else ->
                    Result.failure(
                        DomainException.OperationFailed(
                            operation = "Firestore operation on $resourceName",
                            originalCause = e
                        )
                    )
            }
        } catch (e: Exception) {
            Result.failure(
                DomainException.Unknown(
                    reason = "Unexpected error: ${e.message ?: "No details"}",
                    originalCause = e
                )
            )
        }
    }
}