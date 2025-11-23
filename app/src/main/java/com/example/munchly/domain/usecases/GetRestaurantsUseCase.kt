package com.example.munchly.domain.usecases

import com.example.munchly.data.models.Restaurant
import com.example.munchly.data.repository.RestaurantRepository
import javax.inject.Inject

// Use case for retrieving restaurants for the main feed
class GetRestaurantsUseCase @Inject constructor(
    private val restaurantRepository: RestaurantRepository
) {
    suspend operator fun invoke(limit: Int = 20): List<Restaurant> {
        return restaurantRepository.getRestaurants(limit)
    }
}