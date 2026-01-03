package com.example.munchly.domain.exceptions

// ============================================================================
// DOMAIN EXCEPTIONS - Single source of truth for domain errors
// ============================================================================

/**
 * Base sealed class for all domain-layer exceptions.
 * These exceptions abstract away infrastructure details and represent
 * business-level errors that the UI layer can understand and display.
 */
sealed class DomainException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {

    /**
     * Network connectivity issues (no internet, timeout, etc.)
     */
    data class NetworkError(
        private val originalCause: Throwable? = null
    ) : DomainException(
        message = "Network connection failed. Please check your connection.",
        cause = originalCause
    )

    /**
     * Insufficient permissions to perform operation
     */
    data class PermissionDenied(
        val resource: String,
        private val originalCause: Throwable? = null
    ) : DomainException(
        message = "Permission denied: $resource",
        cause = originalCause
    )

    /**
     * Requested resource not found
     */
    data class ResourceNotFound(
        val resource: String,
        private val originalCause: Throwable? = null
    ) : DomainException(
        message = "$resource not found",
        cause = originalCause
    )

    /**
     * Data validation failed
     */
    data class ValidationError(
        val reason: String
    ) : DomainException(
        message = reason,
        cause = null
    )

    /**
     * Invalid data format or structure
     */
    data class InvalidData(
        val reason: String,
        private val originalCause: Throwable? = null
    ) : DomainException(
        message = reason,
        cause = originalCause
    )

    /**
     * Operation failed (create, update, delete, etc.)
     */
    data class OperationFailed(
        val operation: String,
        private val originalCause: Throwable? = null
    ) : DomainException(
        message = "$operation failed",
        cause = originalCause
    )

    /**
     * Unknown or unexpected error
     */
    data class Unknown(
        val reason: String,
        private val originalCause: Throwable? = null
    ) : DomainException(
        message = reason,
        cause = originalCause
    )
}