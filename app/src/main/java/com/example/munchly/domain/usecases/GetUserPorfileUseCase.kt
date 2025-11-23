package com.example.munchly.domain.usecases

import com.example.munchly.data.models.UserProfile
import com.example.munchly.data.repository.UserProfileRepository
import javax.inject.Inject

// Use case for retrieving complete user profile with statistics
class GetUserProfileUseCase @Inject constructor(
    private val userProfileRepository: UserProfileRepository
) {
    suspend operator fun invoke(userId: String): UserProfile? {
        return userProfileRepository.getUserProfile(userId)
    }
}