package com.example.munchly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.munchly.ui.screens.FoodLoverFeedScreen
import com.example.munchly.ui.screens.LoginScreen
import com.example.munchly.ui.screens.RegisterCredentialsScreen
import com.example.munchly.ui.screens.RegisterUserTypeScreen
import com.example.munchly.ui.screens.RestaurantOwnerFeedScreen
import com.example.munchly.ui.screens.ProfileScreen

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
                composable("register_type") {
                    RegisterUserTypeScreen(navController = navController)
                }
                composable(
                    "register_credentials?userType={userType}",
                    arguments = listOf(
                        navArgument("userType") {
                            type = NavType.StringType
                            defaultValue = "FOOD_LOVER"
                        }
                    )
                ) { backStackEntry ->
                    val userType = backStackEntry.arguments?.getString("userType") ?: "FOOD_LOVER"
                    RegisterCredentialsScreen(
                        navController = navController,
                        userTypeString = userType
                    )
                }
                composable("restaurant_owner_feed") {
                    RestaurantOwnerFeedScreen()
                }
                composable("food_lover_feed") {
                    FoodLoverFeedScreen(navController = navController)
                }
                composable("profile") {
                    ProfileScreen(navController = navController)
                }
            }
        }
    }
}