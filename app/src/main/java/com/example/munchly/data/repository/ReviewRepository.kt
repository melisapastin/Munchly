package com.example.munchly.data.repository

import com.example.munchly.data.models.Reply
import com.example.munchly.data.models.Review
import com.example.munchly.data.remote.ReviewRemoteDataSource
import javax.inject.Inject

class ReviewRepository @Inject constructor(
    private val reviewRemoteDataSource: ReviewRemoteDataSource
) {

    // Get reviews for a specific restaurant
    suspend fun getRestaurantReviews(restaurantId: String): List<Review> {
        return reviewRemoteDataSource.getRestaurantReviews(restaurantId)
    }

    // Get reviews written by a specific user
    suspend fun getUserReviews(userId: String): List<Review> {
        return reviewRemoteDataSource.getUserReviews(userId)
    }

    // Create a new review
    suspend fun createReview(review: Review): Boolean {
        return reviewRemoteDataSource.createReview(review)
    }

    // Add reply to an existing review
    suspend fun addReply(reviewId: String, reply: Reply): Boolean {
        return reviewRemoteDataSource.addReply(reviewId, reply)
    }
}