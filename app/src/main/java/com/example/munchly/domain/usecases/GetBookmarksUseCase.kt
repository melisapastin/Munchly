package com.example.munchly.domain.usecases

import com.example.munchly.data.models.Restaurant
import com.example.munchly.data.repository.BookmarkRepository
import javax.inject.Inject

// Use case for retrieving user's bookmarked restaurants
class GetBookmarksUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    suspend operator fun invoke(userId: String): List<Restaurant> {
        return bookmarkRepository.getBookmarks(userId)
    }
}