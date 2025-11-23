package com.example.munchly.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.munchly.ui.components.AuthButton
import com.example.munchly.ui.components.AuthFieldLabel
import com.example.munchly.ui.components.AuthTextField
import com.example.munchly.ui.viewmodels.LoginViewModel
import com.example.munchly.ui.viewmodels.LoginViewModelFactory
import com.example.munchly.ui.viewmodels.LoginUiState

@Composable
fun LoginScreen(
    navController: NavController
) {
    // Clean ViewModel acquisition - factory handles dependencies
    val viewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory())
    val uiState by viewModel.uiState.collectAsState()

    // Navigation effect - pure navigation logic
    LoginNavigationEffect(
        loginSuccess = uiState.loginSuccess,
        navController = navController
    )

    // Pure UI composition
    LoginScreenContent(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onSignIn = viewModel::signIn,
        navController = navController
    )
}

@Composable
private fun LoginNavigationEffect(
    loginSuccess: Boolean,
    navController: NavController
) {
    LaunchedEffect(loginSuccess) {
        if (loginSuccess) {
            navController.navigate("food_lover_feed") {
                popUpTo("login") { inclusive = true }
            }
        }
    }
}

@Composable
private fun LoginScreenContent(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSignIn: () -> Unit,
    navController: NavController
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
            AppTitle()
            Spacer(modifier = Modifier.height(32.dp))

            LoginForm(
                email = uiState.email,
                password = uiState.password,
                isLoading = uiState.isLoading,
                onEmailChange = onEmailChange,
                onPasswordChange = onPasswordChange,
                onSignIn = onSignIn
            )

            Spacer(modifier = Modifier.height(24.dp))

            SignUpPrompt(navController)

            ErrorMessage(uiState.error)
        }
    }
}

@Composable
private fun ErrorMessage(error: String?) {
    error?.let {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = error,
            color = Color.Red,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

@Composable
private fun AppLogo() {
    Box(
        modifier = Modifier
            .size(80.dp)
            .background(
                color = Color(0xFFD2691E),
                shape = RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "M",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
private fun AppTitle() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Munchly",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF8B4513)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Discover Your Next Favorite Place",
            fontSize = 14.sp,
            color = Color(0xFF8B7355)
        )
    }
}

@Composable
private fun LoginForm(
    email: String,
    password: String,
    isLoading: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSignIn: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
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

        // Sign in button
        if (isLoading) {
            CircularProgressIndicator(
                color = Color(0xFFD2691E),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            AuthButton(
                text = "Sign In",
                onClick = onSignIn,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SignUpPrompt(navController: NavController) {
    Row {
        Text(
            text = "Don't have an account? ",
            fontSize = 14.sp,
            color = Color(0xFF8B7355)
        )
        Text(
            text = "Sign up",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFD2691E),
            modifier = Modifier.clickable {
                navController.navigate("register_type")
            }
        )
    }
}