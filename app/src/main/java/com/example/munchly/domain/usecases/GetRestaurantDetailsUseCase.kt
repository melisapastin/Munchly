package com.example.munchly.domain.usecases

import com.example.munchly.data.models.Restaurant
import com.example.munchly.data.repository.RestaurantRepository
import javax.inject.Inject

// Use case for retrieving detailed restaurant information
class GetRestaurantDetailsUseCase @Inject constructor(
    private val restaurantRepository: RestaurantRepository
) {
    suspend operator fun invoke(restaurantId: String): Restaurant? {
        return restaurantRepository.getRestaurant(restaurantId)
    }
}