package com.example.munchly.domain.usecases

import com.example.munchly.data.models.Restaurant
import com.example.munchly.data.repository.RestaurantRepository
import javax.inject.Inject

// Use case for searching restaurants by name
class SearchRestaurantsUseCase @Inject constructor(
    private val restaurantRepository: RestaurantRepository
) {
    suspend operator fun invoke(query: String): List<Restaurant> {
        return restaurantRepository.searchRestaurants(query)
    }
}