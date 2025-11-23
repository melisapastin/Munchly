package com.example.munchly.domain.usecases

import com.example.munchly.data.models.Review
import com.example.munchly.data.repository.ReviewRepository
import javax.inject.Inject

// Use case for retrieving reviews written by a specific user
class GetUserReviewsUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository
) {
    suspend operator fun invoke(userId: String): List<Review> {
        return reviewRepository.getUserReviews(userId)
    }
}