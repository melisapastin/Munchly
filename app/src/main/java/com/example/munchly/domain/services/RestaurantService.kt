package com.example.munchly.domain.services

import com.example.munchly.domain.models.RestaurantDomain
import com.example.munchly.domain.models.RestaurantInput

/**
 * Domain service that handles business logic for restaurant operations.
 * Encapsulates operations that don't naturally belong to a single entity.
 */
class RestaurantService(
    private val validator: RestaurantValidator = RestaurantValidator,
    private val phoneFormatter: PhoneFormatter = PhoneFormatter
) {

    /**
     * Validates restaurant input according to business rules.
     * @return null if valid, error message if invalid
     */
    fun validateInput(input: RestaurantInput): String? {
        return validator.validate(
            name = input.name,
            description = input.description,
            tags = input.tags,
            address = input.address,
            phone = input.phone,
            openingHours = input.openingHours
        )
    }

    /**
     * Creates a domain model from input, applying business transformations.
     * Handles trimming, formatting, and timestamp generation.
     */
    fun createDomainModel(
        input: RestaurantInput,
        id: String = "",
        createdAt: Long = System.currentTimeMillis()
    ): RestaurantDomain {
        val now = System.currentTimeMillis()

        return RestaurantDomain(
            id = id,
            ownerId = input.ownerId,
            name = input.name.trim(),
            description = input.description.trim(),
            tags = input.tags.map { it.trim() },
            priceRange = input.priceRange,
            address = input.address.trim(),
            phone = phoneFormatter.format(input.phone),
            openingHours = input.openingHours,
            menuPdfUrl = input.menuPdfUrl,
            images = input.images,
            createdAt = createdAt,
            updatedAt = now
        )
    }

    /**
     * Validates a single tag for addition to existing tags.
     * @return null if valid, error message if invalid
     */
    fun validateNewTag(tag: String, existingTags: List<String>): String? {
        return validator.validateTag(tag, existingTags)
    }
}
