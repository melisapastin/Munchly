package com.example.munchly.ui.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.munchly.domain.exceptions.DomainException
import com.example.munchly.domain.models.DayOfWeek
import com.example.munchly.domain.models.DayScheduleDomain
import com.example.munchly.domain.models.PriceRangeDomain
import com.example.munchly.domain.models.RestaurantDomain
import com.example.munchly.domain.models.RestaurantInput
import com.example.munchly.domain.services.RestaurantValidator
import com.example.munchly.domain.usecases.CreateRestaurantUseCase
import com.example.munchly.domain.usecases.GetRestaurantByOwnerIdUseCase
import com.example.munchly.domain.usecases.UpdateRestaurantUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ============================================================================
// STATE
// ============================================================================

/**
 * UI state for restaurant owner profile.
 * Simplified and focused on actual UI concerns.
 */
data class OwnerProfileState(
    // Screen state
    val isLoading: Boolean = true,
    val hasRestaurant: Boolean = false,
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,

    // Current restaurant data (for display mode)
    val restaurant: RestaurantDomain? = null,

    // Form data (for edit mode)
    val name: String = "",
    val description: String = "",
    val tags: List<String> = emptyList(),
    val priceRange: PriceRangeDomain = PriceRangeDomain.MEDIUM,
    val address: String = "",
    val phone: String = "",
    val openingHours: Map<String, DayScheduleDomain> = createDefaultSchedule(),
    val menuPdfUrl: String = "",
    val menuPdfUri: Uri? = null,
    val images: List<String> = emptyList(),

    // Validation errors
    val nameError: String? = null,
    val descriptionError: String? = null,
    val tagsError: String? = null,
    val addressError: String? = null,
    val phoneError: String? = null,
    val openingHoursError: String? = null,

    // UI feedback
    val error: String? = null,
    val successMessage: String? = null,
    val showTagDialog: Boolean = false
)

/**
 * Creates default opening hours schedule.
 */
private fun createDefaultSchedule(): Map<String, DayScheduleDomain> {
    return DayOfWeek.entries.associate {
        it.name to DayScheduleDomain(
            isOpen = false,
            openTime = "09:00",
            closeTime = "22:00"
        )
    }
}

// ============================================================================
// VIEWMODEL
// ============================================================================

/**
 * ViewModel for restaurant owner profile management.
 * Handles both viewing and editing restaurant information.
 */
class OwnerProfileViewModel(
    private val ownerId: String,
    val username: String,
    private val getRestaurantByOwnerUseCase: GetRestaurantByOwnerIdUseCase,
    private val updateRestaurantUseCase: UpdateRestaurantUseCase,
    private val createRestaurantUseCase: CreateRestaurantUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OwnerProfileState())
    val uiState: StateFlow<OwnerProfileState> = _uiState.asStateFlow()

    init {
        loadRestaurant()
    }

    // ========================================================================
    // DATA LOADING
    // ========================================================================

    /**
     * Loads restaurant data from repository.
     */
    fun loadRestaurant() {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val result = getRestaurantByOwnerUseCase(ownerId)

            if (result.isFailure) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = mapErrorToMessage(result.exceptionOrNull())
                    )
                }
                return@launch
            }

            val restaurant = result.getOrNull()

            _uiState.update {
                it.copy(
                    isLoading = false,
                    restaurant = restaurant,
                    hasRestaurant = restaurant != null,
                    // Populate form fields with existing data
                    name = restaurant?.name ?: "",
                    description = restaurant?.description ?: "",
                    tags = restaurant?.tags ?: emptyList(),
                    priceRange = restaurant?.priceRange ?: PriceRangeDomain.MEDIUM,
                    address = restaurant?.address ?: "",
                    phone = restaurant?.phone ?: "",
                    openingHours = restaurant?.openingHours ?: createDefaultSchedule(),
                    menuPdfUrl = restaurant?.menuPdfUrl ?: "",
                    images = restaurant?.images ?: emptyList()
                )
            }
        }
    }

    // ========================================================================
    // EDIT MODE MANAGEMENT
    // ========================================================================

    /**
     * Enters edit mode.
     */
    fun startEditing() {
        _uiState.update { it.copy(isEditing = true) }
    }

    /**
     * Cancels editing and restores original data.
     */
    fun cancelEditing() {
        val restaurant = _uiState.value.restaurant
        _uiState.update {
            it.copy(
                isEditing = false,
                // Restore original data
                name = restaurant?.name ?: "",
                description = restaurant?.description ?: "",
                tags = restaurant?.tags ?: emptyList(),
                priceRange = restaurant?.priceRange ?: PriceRangeDomain.MEDIUM,
                address = restaurant?.address ?: "",
                phone = restaurant?.phone ?: "",
                openingHours = restaurant?.openingHours ?: createDefaultSchedule(),
                menuPdfUrl = restaurant?.menuPdfUrl ?: "",
                menuPdfUri = null,
                images = restaurant?.images ?: emptyList(),
                // Clear errors
                nameError = null,
                descriptionError = null,
                tagsError = null,
                addressError = null,
                phoneError = null,
                openingHoursError = null,
                error = null
            )
        }
    }

    // ========================================================================
    // FORM INPUT HANDLERS
    // ========================================================================

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name, nameError = null) }
    }

    fun onDescriptionChange(description: String) {
        _uiState.update { it.copy(description = description, descriptionError = null) }
    }

    fun onPriceRangeChange(priceRange: PriceRangeDomain) {
        _uiState.update { it.copy(priceRange = priceRange) }
    }

    fun onAddressChange(address: String) {
        _uiState.update { it.copy(address = address, addressError = null) }
    }

    fun onPhoneChange(phone: String) {
        _uiState.update { it.copy(phone = phone, phoneError = null) }
    }

    fun onScheduleChange(dayName: String, schedule: DayScheduleDomain) {
        val currentSchedule = _uiState.value.openingHours
        _uiState.update {
            it.copy(
                openingHours = currentSchedule + (dayName to schedule),
                openingHoursError = null
            )
        }
    }

    // ========================================================================
    // TAG MANAGEMENT
    // ========================================================================

    fun showTagDialog() {
        _uiState.update { it.copy(showTagDialog = true) }
    }

    fun hideTagDialog() {
        _uiState.update { it.copy(showTagDialog = false) }
    }

    fun addTag(tag: String, onResult: (String?) -> Unit) {
        val validationError = RestaurantValidator.validateTag(tag, _uiState.value.tags)
        if (validationError != null) {
            onResult(validationError)
            return
        }

        _uiState.update {
            it.copy(
                tags = it.tags + tag,
                tagsError = null
            )
        }
        onResult(null)
    }

    fun removeTag(tag: String) {
        _uiState.update {
            it.copy(
                tags = it.tags.filter { t -> t != tag },
                tagsError = null
            )
        }
    }

    // ========================================================================
    // FILE MANAGEMENT
    // ========================================================================

    fun onMenuPdfSelected(uri: Uri) {
        // TODO: Upload to Firebase Storage and get URL
        // For now, just store the URI
        _uiState.update { it.copy(menuPdfUri = uri) }
    }

    fun removeMenuPdf() {
        _uiState.update {
            it.copy(
                menuPdfUrl = "",
                menuPdfUri = null
            )
        }
    }

    fun onImageSelected(uri: Uri) {
        // TODO: Upload to Firebase Storage and get URL
        // For now, this feature is not implemented
    }

    fun removeImage(imageUrl: String) {
        _uiState.update {
            it.copy(images = it.images.filter { img -> img != imageUrl })
        }
    }

    // ========================================================================
    // SAVE OPERATION
    // ========================================================================

    /**
     * Saves restaurant data (create or update).
     */
    fun saveRestaurant() {
        val currentState = _uiState.value

        _uiState.update { it.copy(isSaving = true, error = null) }

        viewModelScope.launch {
            // Create input model from current form state
            val input = RestaurantInput(
                ownerId = ownerId,
                name = currentState.name,
                description = currentState.description,
                tags = currentState.tags,
                priceRange = currentState.priceRange,
                address = currentState.address,
                phone = currentState.phone,
                openingHours = currentState.openingHours,
                menuPdfUrl = currentState.menuPdfUrl,
                images = currentState.images
            )

            // Call appropriate use case
            val result = if (currentState.hasRestaurant && currentState.restaurant != null) {
                updateRestaurantUseCase(
                    restaurantId = currentState.restaurant.id,
                    input = input,
                    createdAt = currentState.restaurant.createdAt
                )
            } else {
                createRestaurantUseCase(input)
            }

            _uiState.update { it.copy(isSaving = false) }

            // Handle result
            if (result.isSuccess) {
                val savedRestaurant = result.getOrThrow()
                _uiState.update {
                    it.copy(
                        restaurant = savedRestaurant,
                        hasRestaurant = true,
                        isEditing = false,
                        menuPdfUri = null,
                        successMessage = "Restaurant saved successfully",
                        error = null
                    )
                }
            } else {
                val exception = result.exceptionOrNull()
                _uiState.update {
                    it.copy(error = mapErrorToMessage(exception))
                }
            }
        }
    }

    // ========================================================================
    // UI FEEDBACK
    // ========================================================================

    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }

    // ========================================================================
    // ERROR MAPPING
    // ========================================================================

    /**
     * Maps domain exceptions to user-friendly error messages.
     */
    private fun mapErrorToMessage(exception: Throwable?): String {
        return when (exception) {
            is DomainException.NetworkError ->
                "Network error. Please check your connection"
            is DomainException.ValidationError ->
                exception.reason
            is DomainException.ResourceNotFound ->
                "Restaurant not found"
            is DomainException.PermissionDenied ->
                "Permission denied. Please try logging in again"
            is DomainException.OperationFailed ->
                "Operation failed. Please try again"
            else ->
                "An error occurred. Please try again"
        }
    }
}