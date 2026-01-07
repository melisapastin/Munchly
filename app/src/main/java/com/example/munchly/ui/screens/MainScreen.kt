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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.munchly.MunchlyApplication
import com.example.munchly.domain.models.UserTypeDomain
import com.example.munchly.ui.components.BottomNavBar
import com.example.munchly.ui.components.BottomNavDestination
import com.example.munchly.ui.viewmodels.AchievementsViewModel
import com.example.munchly.ui.viewmodels.BookmarksViewModel
import com.example.munchly.ui.viewmodels.FoodLoverFeedViewModel
import com.example.munchly.ui.viewmodels.OwnerFeedViewModel
import com.example.munchly.ui.viewmodels.OwnerProfileViewModel
import com.example.munchly.ui.viewmodels.RestaurantDetailsViewModel
import com.example.munchly.ui.viewmodels.RestaurantReviewsViewModel
import com.google.firebase.auth.FirebaseAuth

/**
 * Main screen container with bottom navigation.
 * Manages navigation between feed and profile sections for both user types.
 */
@Composable
fun MainScreen(
    userType: UserTypeDomain,
    username: String,
    userId: String,
    navController: NavController
) {
    val app = LocalContext.current.applicationContext as MunchlyApplication
    val innerNavController = rememberNavController()
    val currentDestination = remember { mutableStateOf(BottomNavDestination.FEED) }

    // Create ViewModels based on user type
    val ownerFeedViewModel = remember(userId) {
        if (userType == UserTypeDomain.RESTAURANT_OWNER) {
            OwnerFeedViewModel(
                ownerId = userId,
                getRestaurantByOwnerUseCase = app.getRestaurantByOwnerUseCase,
                getRestaurantStatsUseCase = app.getRestaurantStatsUseCase,
                getRecentReviewsUseCase = app.getRecentReviewsUseCase,
                getRestaurantReviewsUseCase = app.getRestaurantReviewsUseCase,
                        countRestaurantBookmarksUseCase = app.countRestaurantBookmarksUseCase
            )
        } else null
    }

    val ownerProfileViewModel = remember(userId) {
        if (userType == UserTypeDomain.RESTAURANT_OWNER) {
            OwnerProfileViewModel(
                ownerId = userId,
                username = username,
                getRestaurantByOwnerUseCase = app.getRestaurantByOwnerUseCase,
                updateRestaurantUseCase = app.updateRestaurantUseCase,
                createRestaurantUseCase = app.createRestaurantUseCase,
                uploadMenuPdfUseCase = app.uploadMenuPdfUseCase,
                uploadRestaurantImageUseCase = app.uploadRestaurantImageUseCase
            )
        } else null
    }

    val foodLoverFeedViewModel = remember(userId) {
        if (userType == UserTypeDomain.FOOD_LOVER) {
            FoodLoverFeedViewModel(
                userId = userId,
                getAllRestaurantsUseCase = app.getAllRestaurantsUseCase,
                getRestaurantReviewsUseCase = app.getRestaurantReviewsUseCase,
                searchRestaurantsUseCase = app.searchRestaurantsUseCase,
                getUserBookmarksUseCase = app.getUserBookmarksUseCase,
                toggleBookmarkUseCase = app.toggleBookmarkUseCase,
                updateUserStatsUseCase = app.updateUserStatsUseCase
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
            // Feed Screen
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
                    username = username,
                    ownerFeedViewModel = ownerFeedViewModel,
                    foodLoverFeedViewModel = foodLoverFeedViewModel,
                    onNavigateToRatings = { innerNavController.navigate("ratings") },
                    onNavigateToReviews = { innerNavController.navigate("reviews") },
                    onNavigateToRestaurantDetails = { restaurantId ->
                        innerNavController.navigate("restaurant_details/$restaurantId")
                    }
                )
            }

            // Profile Screen
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
                    onLogout = handleLogout,
                    onNavigateToBookmarks = {
                        innerNavController.navigate("bookmarks")
                    },
                    onNavigateToAchievements = {
                        innerNavController.navigate("achievements")
                    }
                )
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
                    slideOutOfContainer(  // Fixed: was slideIntoContainer
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

            // Food Lover-specific screens
            composable(
                "restaurant_details/{restaurantId}",
                arguments = listOf(
                    navArgument("restaurantId") { type = NavType.StringType }
                ),
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
            ) { backStackEntry ->
                val restaurantId = backStackEntry.arguments?.getString("restaurantId") ?: ""

                val viewModel = remember(restaurantId) {
                    RestaurantDetailsViewModel(
                        restaurantId = restaurantId,
                        userId = userId,
                        username = username,
                        getRestaurantDetailsUseCase = app.getRestaurantDetailsUseCase,
                        getRestaurantReviewsUseCase = app.getRestaurantReviewsUseCase,
                        isRestaurantBookmarkedUseCase = app.isRestaurantBookmarkedUseCase,
                        toggleBookmarkUseCase = app.toggleBookmarkUseCase,
                        hasUserReviewedRestaurantUseCase = app.hasUserReviewedRestaurantUseCase,
                        createReviewUseCase = app.createReviewUseCase,
                        updateUserStatsUseCase = app.updateUserStatsUseCase,
                        incrementRestaurantStatsUseCase = app.incrementRestaurantStatsUseCase,
                        incrementRestaurantViewsUseCase = app.incrementRestaurantViewsUseCase
                    )
                }

                RestaurantDetailsScreen(
                    viewModel = viewModel,
                    onBackClick = { innerNavController.popBackStack() },
                    onViewAllReviews = {
                        innerNavController.navigate("restaurant_reviews/$restaurantId")
                    }
                )
            }

            composable(
                "restaurant_reviews/{restaurantId}",
                arguments = listOf(
                    navArgument("restaurantId") { type = NavType.StringType }
                ),
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
            ) { backStackEntry ->
                val restaurantId = backStackEntry.arguments?.getString("restaurantId") ?: ""

                val viewModel = remember(restaurantId) {
                    RestaurantReviewsViewModel(
                        restaurantId = restaurantId,
                        getRestaurantReviewsUseCase = app.getRestaurantReviewsUseCase
                    )
                }

                RestaurantReviewsScreen(
                    viewModel = viewModel,
                    onBackClick = { innerNavController.popBackStack() }
                )
            }

            composable(
                "bookmarks",
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
                val viewModel = remember(userId) {
                    BookmarksViewModel(
                        userId = userId,
                        getUserBookmarksUseCase = app.getUserBookmarksUseCase,
                        getRestaurantDetailsUseCase = app.getRestaurantDetailsUseCase,
                        getRestaurantReviewsUseCase = app.getRestaurantReviewsUseCase,
                        toggleBookmarkUseCase = app.toggleBookmarkUseCase
                    )
                }

                BookmarksScreen(
                    viewModel = viewModel,
                    onBackClick = { innerNavController.popBackStack() },
                    onRestaurantClick = { restaurantId ->
                        innerNavController.navigate("restaurant_details/$restaurantId")
                    }
                )
            }

            composable(
                "achievements",
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
                val viewModel = remember(userId) {
                    AchievementsViewModel(
                        userId = userId,
                        getUserAchievementsUseCase = app.getUserAchievementsUseCase
                    )
                }

                AchievementsScreen(
                    viewModel = viewModel,
                    onBackClick = { innerNavController.popBackStack() }
                )
            }
        }
    }
}