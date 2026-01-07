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
import com.example.munchly.domain.usecases.UploadMenuPdfUseCase
import com.example.munchly.domain.usecases.UploadRestaurantImageUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ============================================================================
// STATE
// ============================================================================

data class OwnerProfileState(
    // Screen state
    val isLoading: Boolean = true,
    val hasRestaurant: Boolean = false,
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val isUploadingFile: Boolean = false,  // ADDED

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
    val showTagDialog: Boolean = false,
    val uploadProgress: String? = null  // ADDED: For showing upload status
)

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
// VIEWMODEL - WITH FILE UPLOAD SUPPORT
// ============================================================================

class OwnerProfileViewModel(
    private val ownerId: String,
    val username: String,
    private val getRestaurantByOwnerUseCase: GetRestaurantByOwnerIdUseCase,
    private val updateRestaurantUseCase: UpdateRestaurantUseCase,
    private val createRestaurantUseCase: CreateRestaurantUseCase,
    private val uploadMenuPdfUseCase: UploadMenuPdfUseCase,  // ADDED
    private val uploadRestaurantImageUseCase: UploadRestaurantImageUseCase  // ADDED
) : ViewModel() {

    private val _uiState = MutableStateFlow(OwnerProfileState())
    val uiState: StateFlow<OwnerProfileState> = _uiState.asStateFlow()

    init {
        loadRestaurant()
    }

    // ========================================================================
    // DATA LOADING
    // ========================================================================

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

    fun startEditing() {
        _uiState.update { it.copy(isEditing = true) }
    }

    fun cancelEditing() {
        val restaurant = _uiState.value.restaurant
        _uiState.update {
            it.copy(
                isEditing = false,
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
    // FILE MANAGEMENT - UPDATED WITH ACTUAL UPLOAD
    // ========================================================================

    /**
     * UPDATED: Now actually uploads the PDF to Firebase Storage
     */
    fun onMenuPdfSelected(uri: Uri) {
        _uiState.update {
            it.copy(
                isUploadingFile = true,
                uploadProgress = "Uploading menu PDF...",
                error = null
            )
        }

        viewModelScope.launch {
            // Need restaurant ID - if creating new restaurant, we'll upload during save
            val restaurantId = _uiState.value.restaurant?.id

            if (restaurantId != null) {
                // Restaurant exists - upload immediately
                val result = uploadMenuPdfUseCase(restaurantId, uri)

                if (result.isSuccess) {
                    val downloadUrl = result.getOrNull()!!
                    _uiState.update {
                        it.copy(
                            menuPdfUrl = downloadUrl,
                            menuPdfUri = null,
                            isUploadingFile = false,
                            uploadProgress = null,
                            successMessage = "Menu PDF uploaded successfully"
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isUploadingFile = false,
                            uploadProgress = null,
                            error = mapErrorToMessage(result.exceptionOrNull())
                        )
                    }
                }
            } else {
                // Restaurant doesn't exist yet - store URI for later upload during save
                _uiState.update {
                    it.copy(
                        menuPdfUri = uri,
                        isUploadingFile = false,
                        uploadProgress = null
                    )
                }
            }
        }
    }

    fun removeMenuPdf() {
        _uiState.update {
            it.copy(
                menuPdfUrl = "",
                menuPdfUri = null
            )
        }
    }

    /**
     * UPDATED: Now actually uploads the image to Firebase Storage
     */
    fun onImageSelected(uri: Uri) {
        _uiState.update {
            it.copy(
                isUploadingFile = true,
                uploadProgress = "Uploading image...",
                error = null
            )
        }

        viewModelScope.launch {
            // Need restaurant ID - if creating new restaurant, we'll upload during save
            val restaurantId = _uiState.value.restaurant?.id

            if (restaurantId != null) {
                // Restaurant exists - upload immediately
                val result = uploadRestaurantImageUseCase(restaurantId, uri)

                if (result.isSuccess) {
                    val downloadUrl = result.getOrNull()!!
                    _uiState.update {
                        it.copy(
                            images = it.images + downloadUrl,
                            isUploadingFile = false,
                            uploadProgress = null,
                            successMessage = "Image uploaded successfully"
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isUploadingFile = false,
                            uploadProgress = null,
                            error = mapErrorToMessage(result.exceptionOrNull())
                        )
                    }
                }
            } else {
                // Restaurant doesn't exist yet - show error
                _uiState.update {
                    it.copy(
                        isUploadingFile = false,
                        uploadProgress = null,
                        error = "Please create your restaurant profile first before uploading images"
                    )
                }
            }
        }
    }

    fun removeImage(imageUrl: String) {
        _uiState.update {
            it.copy(images = it.images.filter { img -> img != imageUrl })
        }
    }

    // ========================================================================
    // SAVE OPERATION - UPDATED TO HANDLE PENDING PDF UPLOAD
    // ========================================================================

    /**
     * UPDATED: Now uploads pending PDF during restaurant creation
     */
    fun saveRestaurant() {
        val currentState = _uiState.value

        _uiState.update { it.copy(isSaving = true, error = null) }

        viewModelScope.launch {
            var menuPdfUrl = currentState.menuPdfUrl

            // If creating new restaurant and there's a pending PDF, we need to upload it first
            // But we need the restaurant ID, so we'll create without PDF first, then update

            val input = RestaurantInput(
                ownerId = ownerId,
                name = currentState.name,
                description = currentState.description,
                tags = currentState.tags,
                priceRange = currentState.priceRange,
                address = currentState.address,
                phone = currentState.phone,
                openingHours = currentState.openingHours,
                menuPdfUrl = menuPdfUrl,
                images = currentState.images
            )

            val result = if (currentState.hasRestaurant && currentState.restaurant != null) {
                // Updating existing restaurant
                updateRestaurantUseCase(
                    restaurantId = currentState.restaurant.id,
                    input = input,
                    createdAt = currentState.restaurant.createdAt
                )
            } else {
                // Creating new restaurant
                createRestaurantUseCase(input)
            }

            _uiState.update { it.copy(isSaving = false) }

            if (result.isSuccess) {
                val savedRestaurant = result.getOrThrow()

                // If there's a pending PDF URI, upload it now
                if (currentState.menuPdfUri != null) {
                    _uiState.update { it.copy(uploadProgress = "Uploading menu PDF...") }

                    val pdfResult = uploadMenuPdfUseCase(savedRestaurant.id, currentState.menuPdfUri)

                    if (pdfResult.isSuccess) {
                        val pdfUrl = pdfResult.getOrNull()!!

                        // Update restaurant with PDF URL
                        val updatedInput = input.copy(menuPdfUrl = pdfUrl)
                        val updateResult = updateRestaurantUseCase(
                            restaurantId = savedRestaurant.id,
                            input = updatedInput,
                            createdAt = savedRestaurant.createdAt
                        )

                        if (updateResult.isSuccess) {
                            _uiState.update {
                                it.copy(
                                    restaurant = updateResult.getOrThrow(),
                                    hasRestaurant = true,
                                    isEditing = false,
                                    menuPdfUri = null,
                                    menuPdfUrl = pdfUrl,
                                    uploadProgress = null,
                                    successMessage = "Restaurant saved and PDF uploaded successfully",
                                    error = null
                                )
                            }
                        }
                    }
                } else {
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