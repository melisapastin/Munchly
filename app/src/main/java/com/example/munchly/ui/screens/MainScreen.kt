package com.example.munchly.ui.screens

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.munchly.MunchlyApplication
import com.example.munchly.ui.components.BottomNavBar
import com.example.munchly.ui.components.BottomNavDestination
import com.example.munchly.ui.viewmodels.OwnerFeedViewModel
import com.example.munchly.ui.viewmodels.OwnerProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.example.munchly.data.models.UserType

/**
 * Main screen container with bottom navigation.
 * Manages navigation between feed and profile sections.
 */
@Composable
fun MainScreen(
    userType: UserType,
    username: String,
    userId: String,
    navController: NavController
) {
    val app = LocalContext.current.applicationContext as MunchlyApplication
    val innerNavController = rememberNavController()
    val currentDestination = remember { mutableStateOf(BottomNavDestination.FEED) }

    // Create ViewModels for restaurant owners
    val ownerFeedViewModel = remember(userId) {
        if (userType == UserType.RESTAURANT_OWNER) {
            OwnerFeedViewModel(
                ownerId = userId,
                getRestaurantByOwnerUseCase = app.getRestaurantByOwnerUseCase,
                getRestaurantStatsUseCase = app.getRestaurantStatsUseCase,
                getRecentReviewsUseCase = app.getRecentReviewsUseCase
            )
        } else null
    }

    val ownerProfileViewModel = remember(userId) {
        if (userType == UserType.RESTAURANT_OWNER) {
            OwnerProfileViewModel(
                ownerId = userId,
                username = username,
                getRestaurantByOwnerUseCase = app.getRestaurantByOwnerUseCase,
                updateRestaurantUseCase = app.updateRestaurantUseCase,
                createRestaurantUseCase = app.createRestaurantUseCase
            )
        } else null
    }

    val handleLogout = {
        FirebaseAuth.getInstance().signOut()
        navController.navigate("login") {
            popUpTo(0) { inclusive = true }
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentDestination = currentDestination.value,
                onDestinationSelected = { destination ->
                    currentDestination.value = destination
                    when (destination) {
                        BottomNavDestination.FEED -> innerNavController.navigate("feed") {
                            popUpTo("feed") { inclusive = true }
                        }
                        BottomNavDestination.PROFILE -> innerNavController.navigate("profile") {
                            popUpTo("feed") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = innerNavController,
            startDestination = "feed",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(
                "feed",
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(300)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    )
                }
            ) {
                FeedScreen(
                    userType = userType,
                    userId = userId,
                    ownerFeedViewModel = ownerFeedViewModel,
                    onNavigateToRatings = { innerNavController.navigate("ratings") },
                    onNavigateToReviews = { innerNavController.navigate("reviews") }
                )
            }

            composable(
                "profile",
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(300)
                    )
                }
            ) {
                ProfileScreen(
                    userType = userType,
                    username = username,
                    userId = userId,
                    ownerProfileViewModel = ownerProfileViewModel,
                    onLogout = handleLogout
                )
            }

            composable(
                "ratings",
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(300)
                    )
                }
            ) {
                ownerFeedViewModel?.let { viewModel ->
                    AllRatingsScreen(
                        viewModel = viewModel,
                        onBackClick = { innerNavController.popBackStack() }
                    )
                }
            }

            composable(
                "reviews",
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(300)
                    )
                }
            ) {
                ownerFeedViewModel?.let { viewModel ->
                    AllReviewsScreen(
                        viewModel = viewModel,
                        onBackClick = { innerNavController.popBackStack() }
                    )
                }
            }
        }
    }
}