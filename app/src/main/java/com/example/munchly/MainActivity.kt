package com.example.munchly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
<<<<<<< HEAD
<<<<<<< HEAD
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
import com.example.munchly.ui.theme.MunchlyTheme

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
=======
=======
>>>>>>> origin/main
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.example.munchly.ui.theme.MunchlyTheme
import com.example.munchly.ui.viewmodels.UserViewModel
import kotlinx.coroutines.launch
import kotlin.getValue

class MainActivity : ComponentActivity() {
    private val viewModel: UserViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MunchlyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        // Example usage
        lifecycleScope.launch {
            viewModel.loadUser("user_123")
            viewModel.userState.collect { user ->
                user?.let {
                    println("Loaded user: ${it.name}")
<<<<<<< HEAD
>>>>>>> origin/main
=======
>>>>>>> origin/main
                }
            }
        }
    }
<<<<<<< HEAD
<<<<<<< HEAD
=======
=======
>>>>>>> origin/main
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MunchlyTheme {
        Greeting("Android")
    }
<<<<<<< HEAD
>>>>>>> origin/main
=======
>>>>>>> origin/main
}