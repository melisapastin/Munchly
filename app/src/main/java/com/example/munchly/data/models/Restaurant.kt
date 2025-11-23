package com.example.munchly.data.models

import java.util.Date

data class Restaurant(
    // Identity
    val id: String,
    val name: String,
    val ownerId: String,  // Links to the restaurant owner's user ID

    // Location
    val address: String,
    val latitude: Double? = null,  // For maps
    val longitude: Double? = null,

    // Structured opening hours
    val openingHours: OpeningHours,

    // Categorization
    val tags: List<String>,
    val cuisineType: String,  // Main category (Italian, Mexican, etc.)
    val priceRange: Int? = null,
    val description: String? = null,

    // Contact
    val phoneNumber: String? = null,
    val website: String? = null,

    // Analytics
    val averageRating: Double = 0.0,
    val totalReviews: Int = 0,

    // Media
    val imageUrls: List<String> = emptyList(),

    // Metadata
    val createdAt: Date = Date(),
    val isActive: Boolean = true
)

data class OpeningHours(
    val monday: TimeRange?,
    val tuesday: TimeRange?,
    val wednesday: TimeRange?,
    val thursday: TimeRange?,
    val friday: TimeRange?,
    val saturday: TimeRange?,
    val sunday: TimeRange?
)

data class TimeRange(
    val open: String,  // "09:00" in 24h format
    val close: String  // "22:00"
)
