package com.example.munchly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.munchly.domain.models.UserTypeDomain
import com.example.munchly.ui.screens.LoginScreen
import com.example.munchly.ui.screens.MainScreen
import com.example.munchly.ui.screens.RegisterCredentialsScreen
import com.example.munchly.ui.screens.RegisterUserTypeScreen
import com.example.munchly.ui.theme.MunchlyTheme

/**
 * Main activity for Munchly app.
 * Handles top-level navigation between auth screens and main app.
 *
 * UPDATED: Uses domain models (UserTypeDomain) instead of data models (UserType).
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MunchlyTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "login"
                ) {
                    // Login screen
                    composable(
                        route = "login",
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
                        LoginScreen(navController = navController)
                    }

                    // Register: select user type
                    composable(
                        route = "register_user_type",
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
                        RegisterUserTypeScreen(navController = navController)
                    }

                    // Register: enter credentials
                    composable(
                        route = "register_credentials/{userType}",
                        arguments = listOf(
                            navArgument("userType") {
                                type = NavType.StringType
                            }
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
                        val userTypeString = backStackEntry.arguments?.getString("userType")
                            ?: "FOOD_LOVER"

                        // Parse domain enum instead of data enum
                        val userType = try {
                            UserTypeDomain.valueOf(userTypeString)
                        } catch (e: IllegalArgumentException) {
                            UserTypeDomain.FOOD_LOVER
                        }

                        RegisterCredentialsScreen(
                            navController = navController,
                            userType = userType
                        )
                    }

                    // Main app screen
                    composable(
                        route = "main",
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
                        val app = LocalContext.current.applicationContext as MunchlyApplication
                        val user = app.currentUser

                        if (user != null) {
                            MainScreen(
                                userType = user.userType,
                                username = user.username,
                                userId = user.uid,
                                navController = navController
                            )
                        } else {
                            // No user found - navigate back to login
                            LaunchedEffect(Unit) {
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}