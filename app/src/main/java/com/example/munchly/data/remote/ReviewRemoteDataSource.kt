package com.example.munchly.data.remote

import android.util.Log
import com.example.munchly.data.models.Review
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

// Collection name already defined in RestaurantRemoteDataSource
// const val REVIEWS = "reviews"

// ============================================================================
// REMOTE DATA SOURCE INTERFACE
// ============================================================================

/**
 * Interface defining remote data operations for reviews.
 * Extends the existing review functionality for food lovers.
 */
interface ReviewRemoteDataSource {

    suspend fun createReview(review: Review): Review

    suspend fun getReviewsByRestaurant(
        restaurantId: String,
        limit: Int? = null
    ): List<Review>

    suspend fun getReviewsByUser(userId: String): List<Review>

    suspend fun getUserReviewForRestaurant(
        userId: String,
        restaurantId: String
    ): Review?

    suspend fun updateReview(review: Review): Review

    suspend fun deleteReview(reviewId: String)
}

// ============================================================================
// FIREBASE IMPLEMENTATION
// ============================================================================

/**
 * Firebase Firestore implementation of ReviewRemoteDataSource.
 * Handles food lover review operations.
 * FIXED: Added comprehensive logging for debugging.
 */
class ReviewRemoteDataSourceImpl(
    private val firestore: FirebaseFirestore
) : ReviewRemoteDataSource {

    companion object {
        private const val TAG = "ReviewRemoteDataSource"
    }

    override suspend fun createReview(review: Review): Review {
        Log.d(TAG, "Creating review for restaurant: ${review.restaurantId}")

        val docRef = firestore
            .collection(FirestoreCollections.REVIEWS)
            .document()

        val reviewWithId = review.copy(id = docRef.id)

        Log.d(TAG, "Generated review ID: ${docRef.id}")

        try {
            docRef.set(reviewWithId).await()
            Log.d(TAG, "Review saved successfully: ${reviewWithId.id}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save review", e)
            throw e
        }

        return reviewWithId
    }

    override suspend fun getReviewsByRestaurant(
        restaurantId: String,
        limit: Int?
    ): List<Review> {
        Log.d(TAG, "Getting reviews for restaurant: $restaurantId, limit: $limit")

        try {
            // TEMPORARY FIX: Removed orderBy to avoid index requirement
            // Sort in memory instead
            val query = firestore
                .collection(FirestoreCollections.REVIEWS)
                .whereEqualTo("restaurantId", restaurantId)

            val querySnapshot = query.get().await()

            Log.d(TAG, "Query returned ${querySnapshot.documents.size} documents")

            val reviews = querySnapshot.documents.mapNotNull { document ->
                try {
                    val review = document.toObject(Review::class.java)
                    if (review == null) {
                        Log.w(TAG, "Document ${document.id} could not be converted to Review")
                    } else {
                        Log.d(TAG, "Parsed review: id=${review.id}, user=${review.userName}, " +
                                "rating=${review.rating}")
                    }
                    review
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing document ${document.id}", e)
                    null
                }
            }

            Log.d(TAG, "Successfully parsed ${reviews.size} reviews")

            // Sort by createdAt descending in memory
            val sortedReviews = reviews.sortedByDescending { it.createdAt }

            // Apply limit if specified
            val finalReviews = if (limit != null && limit > 0) {
                sortedReviews.take(limit)
            } else {
                sortedReviews
            }

            Log.d(TAG, "Returning ${finalReviews.size} reviews after sorting and limiting")
            return finalReviews

        } catch (e: Exception) {
            Log.e(TAG, "Error getting reviews by restaurant", e)
            throw e
        }
    }

    override suspend fun getReviewsByUser(userId: String): List<Review> {
        Log.d(TAG, "Getting reviews for user: $userId")

        try {
            val querySnapshot = firestore
                .collection(FirestoreCollections.REVIEWS)
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            Log.d(TAG, "Found ${querySnapshot.documents.size} reviews for user")

            return querySnapshot.documents.mapNotNull { document ->
                document.toObject(Review::class.java)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting reviews by user", e)
            throw e
        }
    }

    override suspend fun getUserReviewForRestaurant(
        userId: String,
        restaurantId: String
    ): Review? {
        Log.d(TAG, "Checking if user $userId reviewed restaurant $restaurantId")

        try {
            val querySnapshot = firestore
                .collection(FirestoreCollections.REVIEWS)
                .whereEqualTo("userId", userId)
                .whereEqualTo("restaurantId", restaurantId)
                .limit(1)
                .get()
                .await()

            val review = querySnapshot.documents.firstOrNull()
                ?.toObject(Review::class.java)

            Log.d(TAG, if (review != null) {
                "User has reviewed this restaurant: ${review.id}"
            } else {
                "User has not reviewed this restaurant"
            })

            return review
        } catch (e: Exception) {
            Log.e(TAG, "Error checking user review", e)
            throw e
        }
    }

    override suspend fun updateReview(review: Review): Review {
        Log.d(TAG, "Updating review: ${review.id}")

        try {
            firestore
                .collection(FirestoreCollections.REVIEWS)
                .document(review.id)
                .set(review)
                .await()

            Log.d(TAG, "Review updated successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update review", e)
            throw e
        }

        return review
    }

    override suspend fun deleteReview(reviewId: String) {
        Log.d(TAG, "Deleting review: $reviewId")

        try {
            firestore
                .collection(FirestoreCollections.REVIEWS)
                .document(reviewId)
                .delete()
                .await()

            Log.d(TAG, "Review deleted successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete review", e)
            throw e
        }
    }
}