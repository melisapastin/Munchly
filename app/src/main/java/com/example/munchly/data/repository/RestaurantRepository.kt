package com.example.munchly.data.repository

import com.example.munchly.data.models.Restaurant
import com.example.munchly.data.remote.RestaurantRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RestaurantRepository @Inject constructor(
    private val restaurantRemoteDataSource: RestaurantRemoteDataSource
) {

    // Get restaurants for main feed
    suspend fun getRestaurants(limit: Int = 20): List<Restaurant> {
        return restaurantRemoteDataSource.getRestaurants(limit)
    }

    // Search restaurants by name
    suspend fun searchRestaurants(query: String): List<Restaurant> {
        return if (query.isBlank()) {
            getRestaurants() // Show regular feed if empty search
        } else {
            restaurantRemoteDataSource.searchRestaurants(query)
        }
    }

    // Get specific restaurant details
    suspend fun getRestaurant(restaurantId: String): Restaurant? {
        return restaurantRemoteDataSource.getRestaurant(restaurantId)
    }

    // Stream restaurants for real-time updates
    fun getRestaurantsStream(limit: Int = 20): Flow<List<Restaurant>> = flow {
        emit(getRestaurants(limit))
    }
}