package com.example.munchly.data.models

import java.util.Date

data class Review(
    val id: String,              // Unique review identifier
    val userId: String,          // Author of the original review
    val restaurantId: String,    // Restaurant being reviewed
    val rating: Int,
    val title: String? = null,
    val comment: String? = null,
    val likes: Int = 0,
    val replies: List<Reply> = emptyList(),

    // Metadata
    val createdAt: Date = Date(),    // When review was originally posted
    val updatedAt: Date = Date()     // Last modification date
)
