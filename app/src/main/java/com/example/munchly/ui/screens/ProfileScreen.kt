package com.example.munchly.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.munchly.data.models.UserProfile
import com.example.munchly.ui.components.*
import com.example.munchly.ui.viewmodels.ProfileUiState
import com.example.munchly.ui.viewmodels.ProfileViewModel
import com.example.munchly.ui.viewmodels.ProfileViewModelFactory
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController
) {
    val viewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory())
    val uiState = viewModel.uiState.value

    LaunchedEffect(uiState.logoutSuccess) {
        if (uiState.logoutSuccess) {
            navController.navigate("login") {
                popUpTo("profile") { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        color = Color(0xFF8B4513)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentScreen = "profile",
                onHomeClick = { navController.navigate("food_lover_feed") },
                onProfileClick = { /* Already on profile */ }
            )
        }
    ) { paddingValues ->
        ProfileContent(
            uiState = uiState,
            onLogout = viewModel::logout,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun ProfileContent(
    uiState: ProfileUiState,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        uiState.isLoading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFD2691E))
            }
        }

        uiState.userProfile != null -> {
            ProfileContentLoaded(
                userProfile = uiState.userProfile,
                onLogout = onLogout,
                modifier = modifier
            )
        }

        else -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.error ?: "Failed to load profile",
                    color = Color.Red
                )
            }
        }
    }
}

@Composable
private fun ProfileContentLoaded(
    userProfile: UserProfile,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color(0xFFF5F5F5))
    ) {
        ProfileHeader(
            username = userProfile.username,
            email = userProfile.email,
            joinDate = formatJoinDate(userProfile.joinedDate)
        )

        Spacer(modifier = Modifier.height(24.dp))

        StatsCard(
            bookmarksCount = userProfile.bookmarksCount,
            reviewsCount = userProfile.reviewsCount
        )

        Spacer(modifier = Modifier.height(32.dp))

        ProfileActions(onLogout = onLogout)
    }
}

@Composable
private fun ProfileActions(onLogout: () -> Unit) {
    Column {
        ProfileActionButton(
            text = "Edit Profile",
            onClick = { /* TODO */ }
        )

        ProfileActionButton(
            text = "Bookmarked Restaurants",
            onClick = { /* TODO */ }
        )

        ProfileActionButton(
            text = "My Reviews",
            onClick = { /* TODO */ }
        )

        ProfileActionButton(
            text = "Settings",
            onClick = { /* TODO */ }
        )

        Spacer(modifier = Modifier.height(16.dp))

        ProfileActionButton(
            text = "Logout",
            onClick = onLogout,
            isDestructive = true
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// Helper function to format join date
private fun formatJoinDate(joinDate: Date): String {
    val formatter = java.text.SimpleDateFormat("MMM yyyy", java.util.Locale.getDefault())
    return "Joined ${formatter.format(joinDate)}"
}