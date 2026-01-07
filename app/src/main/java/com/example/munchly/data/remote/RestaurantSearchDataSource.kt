package com.example.munchly.data.remote

import com.example.munchly.data.models.Restaurant
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// ============================================================================
// REMOTE DATA SOURCE INTERFACE
// ============================================================================

/**
 * Interface defining remote data operations for restaurant search.
 * Abstracts Firebase-specific implementation details.
 */
interface RestaurantSearchDataSource {

    suspend fun getAllRestaurants(): List<Restaurant>

    suspend fun searchRestaurantsByName(query: String): List<Restaurant>

    suspend fun searchRestaurantsByTag(tag: String): List<Restaurant>

    suspend fun getRestaurantById(restaurantId: String): Restaurant?
}

// ============================================================================
// FIREBASE IMPLEMENTATION
// ============================================================================

/**
 * Firebase Firestore implementation of RestaurantSearchDataSource.
 * Handles restaurant discovery and search operations.
 */
class RestaurantSearchDataSourceImpl(
    private val firestore: FirebaseFirestore
) : RestaurantSearchDataSource {

    override suspend fun getAllRestaurants(): List<Restaurant> {
        val querySnapshot = firestore
            .collection(FirestoreCollections.RESTAURANTS)
            .get()
            .await()

        return querySnapshot.documents.mapNotNull { document ->
            document.toObject(Restaurant::class.java)
        }
    }

    override suspend fun searchRestaurantsByName(query: String): List<Restaurant> {
        // Firestore doesn't support full-text search natively
        // This is a simplified implementation - in production, use Algolia or similar
        val allRestaurants = getAllRestaurants()

        val normalizedQuery = query.lowercase().trim()

        return allRestaurants.filter { restaurant ->
            restaurant.name.lowercase().contains(normalizedQuery)
        }
    }

    override suspend fun searchRestaurantsByTag(tag: String): List<Restaurant> {
        val querySnapshot = firestore
            .collection(FirestoreCollections.RESTAURANTS)
            .whereArrayContains("tags", tag)
            .get()
            .await()

        return querySnapshot.documents.mapNotNull { document ->
            document.toObject(Restaurant::class.java)
        }
    }

    override suspend fun getRestaurantById(restaurantId: String): Restaurant? {
        val document = firestore
            .collection(FirestoreCollections.RESTAURANTS)
            .document(restaurantId)
            .get()
            .await()

        return document.toObject(Restaurant::class.java)
    }
}