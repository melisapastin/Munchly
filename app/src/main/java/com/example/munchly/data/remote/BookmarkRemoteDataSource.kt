package com.example.munchly.data.remote

import com.example.munchly.data.models.Restaurant
import com.example.munchly.data.models.UserRestaurantInteraction
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class BookmarkRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    // Get all restaurants bookmarked by a user
    suspend fun getBookmarks(userId: String): List<Restaurant> {
        return try {
            // Get bookmarked restaurant IDs
            val bookmarkedIds = firestore.collection("user_restaurant_interactions")
                .whereEqualTo("userId", userId)
                .whereEqualTo("isBookmarked", true)
                .get().await()
                .documents.map { it.id }

            if (bookmarkedIds.isEmpty()) return emptyList()

            // Get restaurant details for bookmarked IDs
            firestore.collection("restaurants")
                .whereIn("id", bookmarkedIds)
                .get().await()
                .toObjects(Restaurant::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Toggle bookmark status for a restaurant
    suspend fun toggleBookmark(userId: String, restaurantId: String, bookmarked: Boolean): Boolean = try {
        val interaction = UserRestaurantInteraction(
            userId = userId,
            restaurantId = restaurantId,
            isBookmarked = bookmarked
        )

        firestore.collection("user_restaurant_interactions")
            .document("${userId}_$restaurantId")
            .set(interaction)
            .await()
        true
    } catch (e: Exception) {
        false
    }

    // Check if a restaurant is bookmarked by user
    suspend fun isBookmarked(userId: String, restaurantId: String): Boolean = try {
        firestore.collection("user_restaurant_interactions")
            .document("${userId}_$restaurantId")
            .get().await()
            .toObject(UserRestaurantInteraction::class.java)
            ?.isBookmarked ?: false
    } catch (e: Exception) {
        false
    }

    suspend fun getBookmarksCount(userId: String): Int = try {
        firestore.collection("user_restaurant_interactions")
            .whereEqualTo("userId", userId)
            .whereEqualTo("isBookmarked", true)
            .get().await()
            .size()
    } catch (e: Exception) {
        0
    }
}