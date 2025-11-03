package com.example.munchly

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.munchly.ui.screens.LoginScreen
import com.example.munchly.ui.screens.RegisterUserTypeScreen
import com.example.munchly.ui.screens.RegisterCredentialsScreen
import com.example.munchly.ui.viewmodels.LoginViewModel
import com.example.munchly.ui.viewmodels.RegisterViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Track which screen to show
            var currentScreen by remember { mutableStateOf("login") }

            // ViewModels
            val loginViewModel: LoginViewModel = viewModel()
            val registerViewModel: RegisterViewModel = viewModel()

            when (currentScreen) {
                "login" -> {
                    val email by loginViewModel.email.collectAsState()
                    val password by loginViewModel.password.collectAsState()

                    LoginScreen(
                        email = email,
                        password = password,
                        onEmailChange = { loginViewModel.onEmailChange(it) },
                        onPasswordChange = { loginViewModel.onPasswordChange(it) },
                        onSignInClick = {
                            // For now, just show what was typed
                            Toast.makeText(
                                this,
                                "Email: $email\nPassword: $password",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onGoogleSignInClick = {
                            Toast.makeText(this, "Google sign in", Toast.LENGTH_SHORT).show()
                        },
                        onSignUpClick = {
                            // Navigate to register user type screen
                            currentScreen = "register_type"
                        }
                    )
                }

                "register_type" -> {
                    val selectedUserType by registerViewModel.selectedUserType.collectAsState()

                    RegisterUserTypeScreen(
                        selectedUserType = selectedUserType,
                        onUserTypeSelected = { registerViewModel.onUserTypeSelected(it) },
                        onContinueClick = {
                            // Navigate to credentials screen
                            registerViewModel.onContinueToCredentials()
                            currentScreen = "register_credentials"
                        },
                        onSignInClick = {
                            // Navigate back to login
                            currentScreen = "login"
                        }
                    )
                }

                "register_credentials" -> {
                    val username by registerViewModel.username.collectAsState()
                    val email by registerViewModel.email.collectAsState()
                    val password by registerViewModel.password.collectAsState()

                    RegisterCredentialsScreen(
                        username = username,
                        email = email,
                        password = password,
                        onUsernameChange = { registerViewModel.onUsernameChange(it) },
                        onEmailChange = { registerViewModel.onEmailChange(it) },
                        onPasswordChange = { registerViewModel.onPasswordChange(it) },
                        onSignUpClick = {
                            // For now, just show what was typed
                            Toast.makeText(
                                this,
                                "Username: $username\nEmail: $email\nPassword: $password",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onSignInClick = {
                            // Navigate back to login
                            currentScreen = "login"
                        }
                    )
                }
            }
        }
    }
}