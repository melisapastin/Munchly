package com.example.munchly.data.remote

import com.example.munchly.data.models.Restaurant
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RestaurantRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    // Get restaurants for main feed
    suspend fun getRestaurants(limit: Int = 20): List<Restaurant> = try {
        firestore.collection("restaurants")
            .whereEqualTo("isActive", true)
            .limit(limit.toLong())
            .get().await()
            .toObjects(Restaurant::class.java)
    } catch (e: Exception) {
        emptyList()
    }

    // Search restaurants by name (basic prefix matching)
    suspend fun searchRestaurants(query: String): List<Restaurant> = try {
        firestore.collection("restaurants")
            .whereEqualTo("isActive", true)
            .whereGreaterThanOrEqualTo("name", query)
            .whereLessThanOrEqualTo("name", query + "\uf8ff")
            .get().await()
            .toObjects(Restaurant::class.java)
    } catch (e: Exception) {
        emptyList()
    }

    // Get single restaurant details
    suspend fun getRestaurant(restaurantId: String): Restaurant? = try {
        firestore.collection("restaurants").document(restaurantId).get().await()
            .toObject(Restaurant::class.java)
    } catch (e: Exception) {
        null
    }
}