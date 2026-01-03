package com.example.munchly.ui.components

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utility object for date formatting throughout the UI.
 * Reuses SimpleDateFormat instances for better performance.
 */
object DateFormatters {
    private val reviewDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    /**
     * Formats a timestamp for display in reviews and activities.
     * Example: "Jan 03, 2026"
     */
    fun formatReviewDate(timestamp: Long): String {
        return reviewDateFormat.format(Date(timestamp))
    }
}

/**
 * Helper function for backwards compatibility.
 * Formats timestamp for display in UI components.
 */
fun formatDate(timestamp: Long): String {
    return DateFormatters.formatReviewDate(timestamp)
}