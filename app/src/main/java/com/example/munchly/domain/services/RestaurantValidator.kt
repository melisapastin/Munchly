package com.example.munchly.domain.services

import com.example.munchly.domain.models.DayScheduleDomain

/**
 * Service responsible for validating restaurant data according to business rules.
 * Pure functions with no side effects.
 */
object RestaurantValidator {

    /**
     * Validates all restaurant input fields.
     * @return null if valid, error message if invalid
     */
    fun validate(
        name: String,
        description: String,
        tags: List<String>,
        address: String,
        phone: String,
        openingHours: Map<String, DayScheduleDomain>
    ): String? {
        return validateName(name)
            ?: validateDescription(description)
            ?: validateTags(tags)
            ?: validateAddress(address)
            ?: validatePhone(phone)
            ?: validateOpeningHours(openingHours)
    }

    /**
     * Validates a single tag and checks for duplicates.
     */
    fun validateTag(tag: String, existingTags: List<String>): String? {
        val trimmed = tag.trim()
        return when {
            trimmed.isEmpty() -> "Tag cannot be empty"
            trimmed.length > ValidationLimits.MAX_TAG_LENGTH ->
                "Tag must be less than ${ValidationLimits.MAX_TAG_LENGTH} characters"
            existingTags.any { it.equals(trimmed, ignoreCase = true) } ->
                "Tag already exists"
            else -> null
        }
    }

    // ========================================================================
    // PRIVATE VALIDATION METHODS
    // ========================================================================

    private fun validateName(name: String): String? =
        validateLength(
            value = name,
            fieldName = "Restaurant name",
            minLength = ValidationLimits.MIN_NAME_LENGTH,
            maxLength = ValidationLimits.MAX_NAME_LENGTH
        )

    private fun validateDescription(description: String): String? =
        validateLength(
            value = description,
            fieldName = "Description",
            minLength = ValidationLimits.MIN_DESCRIPTION_LENGTH,
            maxLength = ValidationLimits.MAX_DESCRIPTION_LENGTH
        )

    private fun validateAddress(address: String): String? =
        validateLength(
            value = address,
            fieldName = "Address",
            minLength = ValidationLimits.MIN_ADDRESS_LENGTH,
            maxLength = ValidationLimits.MAX_ADDRESS_LENGTH
        )

    private fun validateLength(
        value: String,
        fieldName: String,
        minLength: Int,
        maxLength: Int
    ): String? {
        return when {
            value.isBlank() -> "$fieldName is required"
            value.length < minLength -> "$fieldName must be at least $minLength characters"
            value.length > maxLength -> "$fieldName must be less than $maxLength characters"
            else -> null
        }
    }

    private fun validateTags(tags: List<String>): String? {
        return when {
            tags.isEmpty() -> "Please add at least one tag"
            tags.size > ValidationLimits.MAX_TAGS_COUNT ->
                "Maximum ${ValidationLimits.MAX_TAGS_COUNT} tags allowed"
            tags.any { it.isBlank() } -> "Tags cannot be empty"
            hasDuplicateTags(tags) -> "Duplicate tags are not allowed"
            else -> tags.firstNotNullOfOrNull { tag ->
                if (tag.length > ValidationLimits.MAX_TAG_LENGTH) {
                    "Tag must be less than ${ValidationLimits.MAX_TAG_LENGTH} characters"
                } else null
            }
        }
    }

    /**
     * Checks if the tag list contains duplicates (case-insensitive).
     */
    private fun hasDuplicateTags(tags: List<String>): Boolean {
        val normalized = tags.map { it.trim().lowercase() }
        return normalized.distinct().size != normalized.size
    }

    private fun validatePhone(phone: String): String? {
        return when {
            phone.isBlank() -> "Phone number is required"
            !PhoneFormatter.isValid(phone) ->
                "Please enter a valid Romanian phone number (e.g., +40 7XX XXX XXX or 07XX XXX XXX)"
            else -> null
        }
    }

    private fun validateOpeningHours(openingHours: Map<String, DayScheduleDomain>): String? {
        return when {
            openingHours.values.none { it.isOpen } ->
                "Please set opening hours for at least one day"
            else -> null
        }
    }
}

/**
 * Validation limits for restaurant data.
 * These constants are used both for validation and for UI displays
 * (e.g., showing character counts in text fields).
 */
object ValidationLimits {
    const val MIN_NAME_LENGTH = 3
    const val MAX_NAME_LENGTH = 50
    const val MIN_DESCRIPTION_LENGTH = 20
    const val MAX_DESCRIPTION_LENGTH = 500
    const val MIN_ADDRESS_LENGTH = 10
    const val MAX_ADDRESS_LENGTH = 200
    const val MAX_TAG_LENGTH = 20
    const val MAX_TAGS_COUNT = 10
}