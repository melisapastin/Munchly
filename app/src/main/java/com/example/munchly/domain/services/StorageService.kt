package com.example.munchly.domain.services

import android.net.Uri

/**
 * Domain service interface for file storage operations.
 * Abstracts the storage implementation (Firebase Storage) from the domain layer.
 */
interface StorageService {

    /**
     * Uploads a restaurant menu PDF file.
     * @param restaurantId The ID of the restaurant
     * @param fileUri The local URI of the PDF file
     * @return The download URL of the uploaded file
     */
    suspend fun uploadMenuPdf(restaurantId: String, fileUri: Uri): Result<String>

    /**
     * Uploads a restaurant image.
     * @param restaurantId The ID of the restaurant
     * @param imageUri The local URI of the image
     * @return The download URL of the uploaded image
     */
    suspend fun uploadRestaurantImage(restaurantId: String, imageUri: Uri): Result<String>

    /**
     * Deletes a file from storage by its URL.
     * @param fileUrl The download URL of the file to delete
     */
    suspend fun deleteFile(fileUrl: String): Result<Unit>

    /**
     * Validates that a URI points to a valid PDF file.
     * @param uri The URI to validate
     * @return true if valid PDF, false otherwise
     */
    fun isValidPdf(uri: Uri): Boolean

    /**
     * Validates that a URI points to a valid image file.
     * @param uri The URI to validate
     * @return true if valid image, false otherwise
     */
    fun isValidImage(uri: Uri): Boolean
}