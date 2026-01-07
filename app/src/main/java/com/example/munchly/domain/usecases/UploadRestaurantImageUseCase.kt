package com.example.munchly.domain.usecases

import android.net.Uri
import com.example.munchly.domain.exceptions.DomainException
import com.example.munchly.domain.services.StorageService

/**
 * Use case for uploading a restaurant image.
 * Validates and uploads the image file to storage.
 */
class UploadRestaurantImageUseCase(
    private val storageService: StorageService
) {
    /**
     * Uploads a restaurant image file.
     *
     * @param restaurantId The ID of the restaurant
     * @param imageUri The local URI of the image file
     * @return The download URL of the uploaded image
     */
    suspend operator fun invoke(restaurantId: String, imageUri: Uri): Result<String> {
        // Validate inputs
        if (restaurantId.isBlank()) {
            return Result.failure(
                DomainException.ValidationError("Restaurant ID is required")
            )
        }

        if (!storageService.isValidImage(imageUri)) {
            return Result.failure(
                DomainException.ValidationError("Please select a valid image file (JPG, PNG, etc.)")
            )
        }

        // Upload file
        return storageService.uploadRestaurantImage(restaurantId, imageUri)
    }
}