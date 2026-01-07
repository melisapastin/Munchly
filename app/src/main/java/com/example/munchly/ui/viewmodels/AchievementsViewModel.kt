package com.example.munchly.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.munchly.domain.exceptions.DomainException
import com.example.munchly.domain.models.AchievementDomain
import com.example.munchly.domain.usecases.GetUserAchievementsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ============================================================================
// STATE
// ============================================================================

/**
 * UI state for achievements screen.
 * Contains earned and all achievements with progress.
 */
data class AchievementsState(
    val isLoading: Boolean = true,
    val earnedAchievements: List<AchievementDomain> = emptyList(),
    val allAchievements: List<AchievementDomain> = emptyList(),
    val error: String? = null
)

// ============================================================================
// VIEWMODEL
// ============================================================================

/**
 * ViewModel for achievements screen.
 * Manages loading and displaying user achievements with progress.
 */
class AchievementsViewModel(
    private val userId: String,
    private val getUserAchievementsUseCase: GetUserAchievementsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AchievementsState())
    val uiState: StateFlow<AchievementsState> = _uiState.asStateFlow()

    init {
        loadAchievements()
    }

    /**
     * Loads user achievements including progress for unearned ones.
     */
    fun loadAchievements() {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val result = getUserAchievementsUseCase(userId)

            if (result.isFailure) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = mapErrorToMessage(result.exceptionOrNull())
                    )
                }
                return@launch
            }

            val achievementsWithProgress = result.getOrNull()
            if (achievementsWithProgress == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load achievements"
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    earnedAchievements = achievementsWithProgress.earned,
                    allAchievements = achievementsWithProgress.all,
                    error = null
                )
            }
        }
    }

    /**
     * Maps domain exceptions to user-friendly error messages.
     */
    private fun mapErrorToMessage(exception: Throwable?): String {
        return when (exception) {
            is DomainException.NetworkError ->
                "Network error. Please check your connection"
            is DomainException.PermissionDenied ->
                "Permission denied. Please try logging in again"
            else ->
                "Failed to load achievements"
        }
    }
}