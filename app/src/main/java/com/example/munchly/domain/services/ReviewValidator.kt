package com.example.munchly.domain.services

/**
 * Service responsible for validating review data according to business rules.
 * Pure functions with no side effects.
 */
object ReviewValidator {

    /**
     * Validates review input before submission.
     * @return null if valid, error message if invalid
     */
    fun validateReview(rating: Double, comment: String): String? {
        // Must provide either rating or comment (or both)
        if (rating == 0.0 && comment.trim().isEmpty()) {
            return "Please provide either a rating or a review comment"
        }

        return validateRating(rating)
            ?: validateComment(comment)
    }

    /**
     * Validates rating value.
     */
    private fun validateRating(rating: Double): String? {
        // Rating of 0 means no rating provided - that's OK
        if (rating == 0.0) {
            return null
        }

        return when {
            rating < ReviewLimits.MIN_RATING ->
                "Rating cannot be less than ${ReviewLimits.MIN_RATING}"
            rating > ReviewLimits.MAX_RATING ->
                "Rating cannot be more than ${ReviewLimits.MAX_RATING}"
            else -> null
        }
    }

    /**
     * Validates review comment.
     */
    private fun validateComment(comment: String): String? {
        val trimmed = comment.trim()

        // Empty comment is OK (rating-only)
        if (trimmed.isEmpty()) {
            return null
        }

        return when {
            trimmed.length < ReviewLimits.MIN_COMMENT_LENGTH ->
                "Review must be at least ${ReviewLimits.MIN_COMMENT_LENGTH} characters"
            trimmed.length > ReviewLimits.MAX_COMMENT_LENGTH ->
                "Review must be less than ${ReviewLimits.MAX_COMMENT_LENGTH} characters"
            else -> null
        }
    }
}

/**
 * Validation limits for reviews.
 */
object ReviewLimits {
    const val MIN_RATING = 1.0
    const val MAX_RATING = 5.0
    const val MIN_COMMENT_LENGTH = 10
    const val MAX_COMMENT_LENGTH = 500
}