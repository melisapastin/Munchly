package com.example.munchly.data.remote

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

/**
 * Interface defining remote data operations for restaurants.
 * Abstracts Firebase-specific implementation details.
 */
interface RestaurantRemoteDataSource {

    suspend fun getRestaurantByOwnerId(ownerId: String): Restaurant?

    suspend fun getRestaurantStats(restaurantId: String): RestaurantStats?

    suspend fun getRecentReviews(restaurantId: String, limit: Int): List<Review>

    suspend fun updateRestaurant(restaurant: Restaurant): Restaurant

    suspend fun createRestaurant(restaurant: Restaurant): Restaurant
}

// ============================================================================
// FIREBASE IMPLEMENTATION
// ============================================================================

/**
 * Firebase Firestore implementation of RestaurantRemoteDataSource.
 * Handles all Firebase-specific logic and data transformations.
 */
class RestaurantRemoteDataSourceImpl(
    private val firestore: FirebaseFirestore
) : RestaurantRemoteDataSource {

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
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .get()
            .await()

        return querySnapshot.documents.map { document ->
            document.toObject(Review::class.java)
                ?: throw IllegalStateException(
                    "Failed to deserialize review document: ${document.id}. " +
                            "Data may be corrupted or schema mismatch."
                )
        }
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
}