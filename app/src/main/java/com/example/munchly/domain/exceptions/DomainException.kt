package com.example.munchly.domain.exceptions

// ============================================================================
// DOMAIN EXCEPTIONS - Single source of truth for domain errors
// ============================================================================

/**
 * Base sealed class for all domain-layer exceptions.
 * These exceptions represent business-level errors that the UI layer can
 * understand and display.
 *
 * Exception messages are intended for developers (logging, debugging).
 * UI layer is responsible for mapping exception types to user-friendly,
 * localized messages.
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
        message = "Network error occurred",
        cause = originalCause
    )

    /**
     * Insufficient permissions to perform operation
     */
    data class PermissionDenied(
        val resource: String,
        private val originalCause: Throwable? = null
    ) : DomainException(
        message = "Permission denied for resource: $resource",
        cause = originalCause
    )

    /**
     * Requested resource not found
     */
    data class ResourceNotFound(
        val resource: String,
        private val originalCause: Throwable? = null
    ) : DomainException(
        message = "Resource not found: $resource",
        cause = originalCause
    )

    /**
     * Data validation failed
     */
    data class ValidationError(
        val reason: String
    ) : DomainException(
        message = "Validation failed: $reason",
        cause = null
    )

    /**
     * Invalid data format or structure
     */
    data class InvalidData(
        val reason: String,
        private val originalCause: Throwable? = null
    ) : DomainException(
        message = "Invalid data: $reason",
        cause = originalCause
    )

    /**
     * Operation failed (create, update, delete, etc.)
     */
    data class OperationFailed(
        val operation: String,
        private val originalCause: Throwable? = null
    ) : DomainException(
        message = "Operation failed: $operation",
        cause = originalCause
    )

    /**
     * Unknown or unexpected error
     */
    data class Unknown(
        val reason: String,
        private val originalCause: Throwable? = null
    ) : DomainException(
        message = "Unknown error: $reason",
        cause = originalCause
    )
}