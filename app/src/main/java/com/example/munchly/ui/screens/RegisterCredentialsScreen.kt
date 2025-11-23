package com.example.munchly.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.munchly.data.models.UserType
import com.example.munchly.ui.components.AppLogo
import com.example.munchly.ui.components.AppTitle
import com.example.munchly.ui.components.AuthButton
import com.example.munchly.ui.components.AuthFieldLabel
import com.example.munchly.ui.components.AuthTextField
import com.example.munchly.ui.components.ErrorMessage
import com.example.munchly.ui.components.SignInPrompt
import com.example.munchly.ui.viewmodels.RegisterUiState
import com.example.munchly.ui.viewmodels.RegisterViewModel
import com.example.munchly.ui.viewmodels.RegisterViewModelFactory

@Composable
fun RegisterCredentialsScreen(
    navController: NavController,
    userTypeString: String
) {
    // Parse user type from navigation arguments
    val userType = when (userTypeString) {
        "RESTAURANT_OWNER" -> UserType.RESTAURANT_OWNER
        else -> UserType.FOOD_LOVER
    }

    android.util.Log.d("RegisterCredentialsScreen", "Received userType: $userTypeString")
    android.util.Log.d("RegisterCredentialsScreen", "Parsed userType: $userType")

    val viewModel: RegisterViewModel = viewModel(factory = RegisterViewModelFactory())
    val uiState by viewModel.uiState.collectAsState()

    // Handle successful registration navigation
    RegisterNavigationEffect(
        registrationSuccess = uiState.registrationSuccess,
        userType = userType,
        navController = navController
    )

    RegisterCredentialsScreenContent(
        uiState = uiState,
        userType = userType,
        onUsernameChange = viewModel::onUsernameChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onRegister = { viewModel.register(userType) },
        onNavigateToLogin = { navController.navigate("login") }
    )
}

@Composable
private fun RegisterNavigationEffect(
    registrationSuccess: Boolean,
    userType: UserType,
    navController: NavController
) {
    LaunchedEffect(registrationSuccess) {
        if (registrationSuccess) {
            android.util.Log.d("RegisterNavigation", "Registration success! UserType: $userType")
            when (userType) {
                UserType.RESTAURANT_OWNER -> {
                    android.util.Log.d("RegisterNavigation", "Navigating to restaurant_owner_feed")
                    navController.navigate("restaurant_owner_feed") {
                        popUpTo("login") { inclusive = true }
                    }
                }
                else -> {
                    android.util.Log.d("RegisterNavigation", "Navigating to food_lover_feed")
                    navController.navigate("food_lover_feed") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }
        }
    }
}

@Composable
private fun RegisterCredentialsScreenContent(
    uiState: RegisterUiState,
    userType: UserType,
    onUsernameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRegister: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8D5C4))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppLogo()
            Spacer(modifier = Modifier.height(24.dp))
            AppTitle(
                title = "Munchly",
                subtitle = "Create Your ${if (userType == UserType.RESTAURANT_OWNER) "Restaurant Owner" else "Food Lover"} Account"
            )
            Spacer(modifier = Modifier.height(32.dp))

            CredentialsForm(
                username = uiState.username,
                email = uiState.email,
                password = uiState.password,
                isLoading = uiState.isLoading,
                onUsernameChange = onUsernameChange,
                onEmailChange = onEmailChange,
                onPasswordChange = onPasswordChange,
                onRegister = onRegister
            )

            Spacer(modifier = Modifier.height(24.dp))

            SignInPrompt(onNavigateToLogin)

            ErrorMessage(uiState.error)
        }
    }
}

@Composable
private fun CredentialsForm(
    username: String,
    email: String,
    password: String,
    isLoading: Boolean,
    onUsernameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRegister: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Username field
        AuthFieldLabel("Username")
        Spacer(modifier = Modifier.height(8.dp))
        AuthTextField(
            value = username,
            onValueChange = onUsernameChange,
            placeholder = "Choose a username"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Email field
        AuthFieldLabel("Email")
        Spacer(modifier = Modifier.height(8.dp))
        AuthTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = "your@email.com"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password field
        AuthFieldLabel("Password")
        Spacer(modifier = Modifier.height(8.dp))
        AuthTextField(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = "········",
            isPassword = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Sign up button
        if (isLoading) {
            CircularProgressIndicator(
                color = Color(0xFFD2691E),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            AuthButton(
                text = "Sign Up",
                onClick = onRegister,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}