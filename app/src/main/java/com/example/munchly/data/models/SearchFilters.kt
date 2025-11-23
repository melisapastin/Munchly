package com.example.munchly.data.models

data class SearchFilters(
    val query: String = "",          // Text search (name/tags)
    val cuisineTypes: List<String> = emptyList(),  // Filter by cuisine
    val minRating: Double = 0.0,    // Minimum average rating
    val priceRange: IntRange? = null,  // Price range (1-4)
    val isOpenNow: Boolean = false   // Only show currently open
)