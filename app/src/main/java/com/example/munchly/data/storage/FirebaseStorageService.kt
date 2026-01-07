package com.example.munchly.data.storage

import android.content.ContentResolver
import android.net.Uri
import android.webkit.MimeTypeMap
import com.example.munchly.domain.exceptions.DomainException
import com.example.munchly.domain.services.StorageService
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Firebase Storage implementation of StorageService.
 * Handles file uploads and deletions to Firebase Storage.
 */
class FirebaseStorageService(
    private val storage: FirebaseStorage,
    private val contentResolver: ContentResolver
) : StorageService {

    companion object {
        private const val MENU_PDFS_PATH = "restaurants/{restaurantId}/menu"
        private const val RESTAURANT_IMAGES_PATH = "restaurants/{restaurantId}/images"
        private const val MAX_PDF_SIZE = 10 * 1024 * 1024 // 10MB
        private const val MAX_IMAGE_SIZE = 5 * 1024 * 1024 // 5MB
    }

    /**
     * Uploads a menu PDF to Firebase Storage.
     */
    override suspend fun uploadMenuPdf(restaurantId: String, fileUri: Uri): Result<String> {
        return try {
            // Validate file
            if (!isValidPdf(fileUri)) {
                return Result.failure(
                    DomainException.ValidationError("Invalid PDF file")
                )
            }

            // Check file size
            val fileSize = getFileSize(fileUri)
            if (fileSize > MAX_PDF_SIZE) {
                return Result.failure(
                    DomainException.ValidationError("PDF file too large (max 10MB)")
                )
            }

            // Generate unique filename
            val filename = "menu_${System.currentTimeMillis()}.pdf"
            val path = MENU_PDFS_PATH.replace("{restaurantId}", restaurantId)
            val storageRef = storage.reference.child("$path/$filename")

            // Upload file
            val uploadTask = storageRef.putFile(fileUri).await()

            // Get download URL
            val downloadUrl = storageRef.downloadUrl.await()

            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.failure(
                DomainException.OperationFailed(
                    operation = "Upload menu PDF",
                    originalCause = e
                )
            )
        }
    }

    /**
     * Uploads a restaurant image to Firebase Storage.
     */
    override suspend fun uploadRestaurantImage(restaurantId: String, imageUri: Uri): Result<String> {
        return try {
            // Validate file
            if (!isValidImage(imageUri)) {
                return Result.failure(
                    DomainException.ValidationError("Invalid image file")
                )
            }

            // Check file size
            val fileSize = getFileSize(imageUri)
            if (fileSize > MAX_IMAGE_SIZE) {
                return Result.failure(
                    DomainException.ValidationError("Image file too large (max 5MB)")
                )
            }

            // Generate unique filename
            val extension = getFileExtension(imageUri) ?: "jpg"
            val filename = "image_${UUID.randomUUID()}.$extension"
            val path = RESTAURANT_IMAGES_PATH.replace("{restaurantId}", restaurantId)
            val storageRef = storage.reference.child("$path/$filename")

            // Upload file
            val uploadTask = storageRef.putFile(imageUri).await()

            // Get download URL
            val downloadUrl = storageRef.downloadUrl.await()

            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.failure(
                DomainException.OperationFailed(
                    operation = "Upload restaurant image",
                    originalCause = e
                )
            )
        }
    }

    /**
     * Deletes a file from Firebase Storage by its URL.
     */
    override suspend fun deleteFile(fileUrl: String): Result<Unit> {
        return try {
            val storageRef = storage.getReferenceFromUrl(fileUrl)
            storageRef.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            // If file doesn't exist, that's okay
            Result.success(Unit)
        }
    }

    /**
     * Validates that a URI points to a PDF file.
     */
    override fun isValidPdf(uri: Uri): Boolean {
        val mimeType = getMimeType(uri)
        return mimeType == "application/pdf"
    }

    /**
     * Validates that a URI points to an image file.
     */
    override fun isValidImage(uri: Uri): Boolean {
        val mimeType = getMimeType(uri)
        return mimeType?.startsWith("image/") == true
    }

    // ========================================================================
    // HELPER METHODS
    // ========================================================================

    /**
     * Gets the MIME type of a file from its URI.
     */
    private fun getMimeType(uri: Uri): String? {
        return if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            contentResolver.getType(uri)
        } else {
            val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.lowercase())
        }
    }

    /**
     * Gets the file extension from a URI.
     */
    private fun getFileExtension(uri: Uri): String? {
        val mimeType = getMimeType(uri) ?: return null
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
    }

    /**
     * Gets the size of a file in bytes.
     */
    private fun getFileSize(uri: Uri): Long {
        return try {
            contentResolver.openFileDescriptor(uri, "r")?.use { descriptor ->
                descriptor.statSize
            } ?: 0L
        } catch (e: Exception) {
            0L
        }
    }
}