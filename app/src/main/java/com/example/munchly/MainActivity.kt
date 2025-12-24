package com.example.munchly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.munchly.data.models.UserType
import com.example.munchly.ui.screens.LoginScreen
import com.example.munchly.ui.screens.MainScreen
import com.example.munchly.ui.screens.RegisterUserTypeScreen
import com.example.munchly.ui.screens.RegisterCredentialsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "login"
            ) {
                composable(
                    "login",
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

                composable(
                    "register_user_type",
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
                    val userTypeString = backStackEntry.arguments?.getString("userType") ?: "FOOD_LOVER"
                    val userType = UserType.valueOf(userTypeString)

                    RegisterCredentialsScreen(
                        navController = navController,
                        userType = userType
                    )
                }

                composable(
                    route = "main/{userType}/{username}",
                    arguments = listOf(
                        navArgument("userType") { type = NavType.StringType },
                        navArgument("username") { type = NavType.StringType }
                    ),
                    enterTransition = {
                        fadeIn(animationSpec = tween(300))
                    },
                    exitTransition = {
                        fadeOut(animationSpec = tween(300))
                    }
                ) { backStackEntry ->
                    val userTypeString = backStackEntry.arguments?.getString("userType") ?: "FOOD_LOVER"
                    val username = backStackEntry.arguments?.getString("username") ?: ""
                    val userType = UserType.valueOf(userTypeString)

                    MainScreen(userType = userType, username = username)
                }
            }
        }
    }
}