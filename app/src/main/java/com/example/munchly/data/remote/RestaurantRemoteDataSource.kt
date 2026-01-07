package com.example.munchly.data.remote

import android.util.Log
import com.example.munchly.data.models.Restaurant
import com.example.munchly.data.models.RestaurantStats
import com.example.munchly.data.models.Review
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

// ============================================================================
// FIRESTORE COLLECTION NAMES
// ============================================================================

internal object FirestoreCollections {
    const val RESTAURANTS = "restaurants"
    const val RESTAURANT_STATS = "restaurant_stats"
    const val REVIEWS = "reviews"
}

// ============================================================================
// REMOTE DATA SOURCE INTERFACE
// ============================================================================

interface RestaurantRemoteDataSource {
    suspend fun getRestaurantByOwnerId(ownerId: String): Restaurant?
    suspend fun getRestaurantStats(restaurantId: String): RestaurantStats?
    suspend fun getRecentReviews(restaurantId: String, limit: Int): List<Review>
    suspend fun updateRestaurant(restaurant: Restaurant): Restaurant
    suspend fun createRestaurant(restaurant: Restaurant): Restaurant
    suspend fun incrementRestaurantStats(restaurantId: String, newRating: Double, hasComment: Boolean)
    suspend fun incrementRestaurantViews(restaurantId: String)
}

// ============================================================================
// FIREBASE IMPLEMENTATION
// ============================================================================

/**
 * FIXED: Average rating now only considers ratings > 0
 */
class RestaurantRemoteDataSourceImpl(
    private val firestore: FirebaseFirestore
) : RestaurantRemoteDataSource {

    companion object {
        private const val TAG = "RestaurantRemoteDS"
    }

    override suspend fun getRestaurantByOwnerId(ownerId: String): Restaurant? {
        val querySnapshot = firestore
            .collection(FirestoreCollections.RESTAURANTS)
            .whereEqualTo("ownerId", ownerId)
            .limit(1)
            .get()
            .await()

        return querySnapshot
            .documents
            .firstOrNull()
            ?.toObject(Restaurant::class.java)
    }

    override suspend fun getRestaurantStats(restaurantId: String): RestaurantStats? {
        val statsDoc = firestore
            .collection(FirestoreCollections.RESTAURANT_STATS)
            .document(restaurantId)
            .get()
            .await()

        return statsDoc.toObject(RestaurantStats::class.java)
    }

    override suspend fun getRecentReviews(
        restaurantId: String,
        limit: Int
    ): List<Review> {
        val querySnapshot = firestore
            .collection(FirestoreCollections.REVIEWS)
            .whereEqualTo("restaurantId", restaurantId)
            .get()
            .await()

        val reviews = querySnapshot.documents.mapNotNull { document ->
            document.toObject(Review::class.java)
        }

        return reviews.sortedByDescending { it.createdAt }.take(limit)
    }

    override suspend fun updateRestaurant(restaurant: Restaurant): Restaurant {
        firestore
            .collection(FirestoreCollections.RESTAURANTS)
            .document(restaurant.id)
            .set(restaurant, SetOptions.merge())
            .await()

        return restaurant
    }

    override suspend fun createRestaurant(restaurant: Restaurant): Restaurant {
        val docRef = firestore
            .collection(FirestoreCollections.RESTAURANTS)
            .document()

        val restaurantWithId = restaurant.copy(id = docRef.id)

        docRef.set(restaurantWithId).await()

        return restaurantWithId
    }

    /**
     * FIXED: Only counts ratings > 0 for average calculation.
     *
     * Logic:
     * - If rating > 0: increment totalRatings and update average
     * - If rating = 0 (review-only): don't affect totalRatings or average
     * - If comment exists: increment totalReviews
     */
    override suspend fun incrementRestaurantStats(
        restaurantId: String,
        newRating: Double,
        hasComment: Boolean
    ) {
        Log.d(TAG, "Incrementing stats - restaurantId: $restaurantId, rating: $newRating, hasComment: $hasComment")

        val statsDoc = firestore
            .collection(FirestoreCollections.RESTAURANT_STATS)
            .document(restaurantId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(statsDoc)

            if (snapshot.exists()) {
                val currentStats = snapshot.toObject(RestaurantStats::class.java)
                if (currentStats != null) {
                    Log.d(TAG, "Current stats - totalRatings: ${currentStats.totalRatings}, " +
                            "averageRating: ${currentStats.averageRating}, totalReviews: ${currentStats.totalReviews}")

                    // FIXED: Only increment rating count if rating > 0
                    val newTotalRatings = if (newRating > 0) {
                        currentStats.totalRatings + 1
                    } else {
                        currentStats.totalRatings
                    }

                    // FIXED: Only update average if rating > 0
                    val newAverageRating = if (newRating > 0 && newTotalRatings > 0) {
                        // Calculate new average: (old_sum + new_rating) / new_count
                        val oldSum = currentStats.averageRating * currentStats.totalRatings
                        val newSum = oldSum + newRating
                        newSum / newTotalRatings
                    } else {
                        // No change to average if rating is 0 (review-only)
                        currentStats.averageRating
                    }

                    // Increment review count if there's a comment
                    val newTotalReviews = if (hasComment) {
                        currentStats.totalReviews + 1
                    } else {
                        currentStats.totalReviews
                    }

                    val updatedStats = currentStats.copy(
                        totalRatings = newTotalRatings,
                        totalReviews = newTotalReviews,
                        averageRating = newAverageRating,
                        lastUpdated = System.currentTimeMillis()
                    )

                    Log.d(TAG, "New stats - totalRatings: ${updatedStats.totalRatings}, " +
                            "averageRating: ${updatedStats.averageRating}, totalReviews: ${updatedStats.totalReviews}")

                    transaction.set(statsDoc, updatedStats)
                }
            } else {
                // Create initial stats
                // FIXED: Only set rating stats if rating > 0
                val initialStats = if (newRating > 0) {
                    RestaurantStats(
                        restaurantId = restaurantId,
                        totalReviews = if (hasComment) 1 else 0,
                        totalRatings = 1,
                        averageRating = newRating,
                        totalBookmarks = 0,
                        monthlyViews = 0,
                        lastUpdated = System.currentTimeMillis()
                    )
                } else {
                    RestaurantStats(
                        restaurantId = restaurantId,
                        totalReviews = if (hasComment) 1 else 0,
                        totalRatings = 0,
                        averageRating = 0.0,
                        totalBookmarks = 0,
                        monthlyViews = 0,
                        lastUpdated = System.currentTimeMillis()
                    )
                }

                Log.d(TAG, "Creating initial stats: $initialStats")
                transaction.set(statsDoc, initialStats)
            }
        }.await()
    }

    override suspend fun incrementRestaurantViews(restaurantId: String) {
        val statsDoc = firestore
            .collection(FirestoreCollections.RESTAURANT_STATS)
            .document(restaurantId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(statsDoc)

            if (snapshot.exists()) {
                val currentStats = snapshot.toObject(RestaurantStats::class.java)
                if (currentStats != null) {
                    val updatedStats = currentStats.copy(
                        monthlyViews = currentStats.monthlyViews + 1,
                        lastUpdated = System.currentTimeMillis()
                    )
                    transaction.set(statsDoc, updatedStats)
                }
            } else {
                val initialStats = RestaurantStats(
                    restaurantId = restaurantId,
                    totalReviews = 0,
                    totalRatings = 0,
                    averageRating = 0.0,
                    totalBookmarks = 0,
                    monthlyViews = 1,
                    lastUpdated = System.currentTimeMillis()
                )
                transaction.set(statsDoc, initialStats)
            }
        }.await()
    }
}