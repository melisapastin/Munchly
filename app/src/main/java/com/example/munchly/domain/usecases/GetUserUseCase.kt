package com.example.munchly.domain.usecases

import com.example.munchly.domain.managers.UserManager

class GetUserUseCase(private val userManager: UserManager) {
    suspend fun execute(userId: String) = userManager.getFormattedUser(userId)
}
