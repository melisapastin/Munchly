package com.example.munchly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.munchly.ui.screens.LoginScreen
import com.example.munchly.ui.screens.RegisterCredentialsScreen
import com.example.munchly.ui.screens.RegisterUserTypeScreen
import com.example.munchly.ui.viewmodels.RegisterViewModel

class MainActivity : ComponentActivity() {
    private val registerViewModel: RegisterViewModel by viewModels()

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
                    RegisterUserTypeScreen(
                        navController = navController,
                        viewModel = registerViewModel  // Pass the same instance
                    )
                }
                composable("register_credentials") {
                    RegisterCredentialsScreen(
                        navController = navController,
                        viewModel = registerViewModel  // Pass the same instance
                    )
                }
            }
        }
    }
}