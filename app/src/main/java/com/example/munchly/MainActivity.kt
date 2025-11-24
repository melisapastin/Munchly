package com.example.munchly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
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
                composable("login") {
                    LoginScreen(navController = navController)
                }
                composable("register_user_type") {
                    RegisterUserTypeScreen(navController = navController)
                }
                composable(
                    route = "register_credentials/{userType}",
                    arguments = listOf(
                        navArgument("userType") {
                            type = NavType.StringType
                        }
                    )
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
                    )
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