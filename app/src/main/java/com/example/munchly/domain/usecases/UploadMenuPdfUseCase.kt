package com.example.munchly.domain.usecases

import android.net.Uri
import com.example.munchly.domain.exceptions.DomainException
import com.example.munchly.domain.services.StorageService

/**
 * Use case for uploading a restaurant menu PDF.
 * Validates and uploads the PDF file to storage.
 */
class UploadMenuPdfUseCase(
    private val storageService: StorageService
) {
    /**
     * Uploads a menu PDF file.
     *
     * @param restaurantId The ID of the restaurant
     * @param fileUri The local URI of the PDF file
     * @return The download URL of the uploaded PDF
     */
    suspend operator fun invoke(restaurantId: String, fileUri: Uri): Result<String> {
        // Validate inputs
        if (restaurantId.isBlank()) {
            return Result.failure(
                DomainException.ValidationError("Restaurant ID is required")
            )
        }

        if (!storageService.isValidPdf(fileUri)) {
            return Result.failure(
                DomainException.ValidationError("Please select a valid PDF file")
            )
        }

        // Upload file
        return storageService.uploadMenuPdf(restaurantId, fileUri)
    }
}