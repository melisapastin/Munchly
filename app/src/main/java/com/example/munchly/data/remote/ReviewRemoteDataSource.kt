package com.example.munchly.data.remote

import com.example.munchly.data.models.Review
import com.example.munchly.data.models.Reply
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ReviewRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    // Get reviews for a specific restaurant
    suspend fun getRestaurantReviews(restaurantId: String): List<Review> = try {
        firestore.collection("reviews")
            .whereEqualTo("restaurantId", restaurantId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get().await()
            .toObjects(Review::class.java)
    } catch (e: Exception) {
        emptyList()
    }

    // Get reviews written by a specific user
    suspend fun getUserReviews(userId: String): List<Review> = try {
        firestore.collection("reviews")
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get().await()
            .toObjects(Review::class.java)
    } catch (e: Exception) {
        emptyList()
    }

    // Create a new review
    suspend fun createReview(review: Review): Boolean = try {
        firestore.collection("reviews").document(review.id).set(review).await()
        true
    } catch (e: Exception) {
        false
    }

    // Add a reply to an existing review
    suspend fun addReply(reviewId: String, reply: Reply): Boolean = try {
        val reviewRef = firestore.collection("reviews").document(reviewId)

        firestore.runTransaction { transaction ->
            val review = transaction.get(reviewRef).toObject(Review::class.java)
                ?: throw Exception("Review not found")
            transaction.update(reviewRef, "replies", review.replies + reply)
        }.await()
        true
    } catch (e: Exception) {
        false
    }

    suspend fun getReviewsCount(userId: String): Int = try {
        firestore.collection("reviews")
            .whereEqualTo("userId", userId)
            .get().await()
            .size()
    } catch (e: Exception) {
        0
    }
}