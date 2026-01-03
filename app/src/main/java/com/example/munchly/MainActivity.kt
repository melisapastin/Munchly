package com.example.munchly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.munchly.ui.screens.LoginScreen
import com.example.munchly.ui.screens.MainScreen
import com.example.munchly.ui.screens.RegisterCredentialsScreen
import com.example.munchly.ui.screens.RegisterUserTypeScreen
import com.example.munchly.data.models.UserType
import com.example.munchly.ui.theme.MunchlyTheme

/**
 * Main activity for Munchly app.
 * Handles top-level navigation between auth screens and main app.
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
                        val userType = try {
                            UserType.valueOf(userTypeString)
                        } catch (e: IllegalArgumentException) {
                            UserType.FOOD_LOVER
                        }

                        RegisterCredentialsScreen(
                            navController = navController,
                            userType = userType
                        )
                    }

                    // Main app screen
                    composable(
                        route = "main/{userType}/{userId}/{username}",
                        arguments = listOf(
                            navArgument("userType") { type = NavType.StringType },
                            navArgument("userId") { type = NavType.StringType },
                            navArgument("username") { type = NavType.StringType }
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
                        val userId = backStackEntry.arguments?.getString("userId") ?: ""
                        val username = backStackEntry.arguments?.getString("username") ?: ""

                        val userType = try {
                            UserType.valueOf(userTypeString)
                        } catch (e: IllegalArgumentException) {
                            UserType.FOOD_LOVER
                        }

                        MainScreen(
                            userType = userType,
                            username = username,
                            userId = userId,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}