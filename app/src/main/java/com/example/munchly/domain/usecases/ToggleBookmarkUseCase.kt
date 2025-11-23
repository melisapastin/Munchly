package com.example.munchly.domain.usecases

import com.example.munchly.data.repository.BookmarkRepository
import javax.inject.Inject

// Use case for toggling restaurant bookmark status
class ToggleBookmarkUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    suspend operator fun invoke(userId: String, restaurantId: String): Boolean {
        return bookmarkRepository.toggleBookmark(userId, restaurantId)
    }
}