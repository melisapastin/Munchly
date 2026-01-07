package com.example.munchly.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.munchly.MunchlyApplication
import com.example.munchly.ui.components.AppLogo
import com.example.munchly.ui.components.AppTitle
import com.example.munchly.ui.components.AuthButton
import com.example.munchly.ui.components.AuthFieldLabel
import com.example.munchly.ui.components.SignUpPrompt
import com.example.munchly.ui.components.ValidationTextField
import com.example.munchly.ui.theme.MunchlyColors
import com.example.munchly.ui.viewmodels.LoginViewModel

/**
 * Login screen for existing users.
 * Uses domain models (UserDomain) exclusively.
 */
@Composable
fun LoginScreen(navController: NavController) {
    val app = LocalContext.current.applicationContext as MunchlyApplication
    val viewModel = remember { LoginViewModel(app.loginUseCase) }
    val state by viewModel.uiState.collectAsState()

    // Navigate to main screen on successful login
    LaunchedEffect(state.loginSuccess) {
        if (state.loginSuccess) {
            state.user?.let { user ->
                app.setCurrentUser(user)
                navController.navigate("main") {
                    popUpTo("login") { inclusive = true }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MunchlyColors.background)
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
                title = "Welcome Back",
                subtitle = "Sign in to your account"
            )
            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MunchlyColors.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    AuthFieldLabel("Email")
                    Spacer(modifier = Modifier.height(8.dp))
                    ValidationTextField(
                        value = state.email,
                        onValueChange = viewModel::onEmailChange,
                        placeholder = "your@email.com",
                        isError = state.emailError != null,
                        errorMessage = state.emailError,
                        imeAction = ImeAction.Next
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AuthFieldLabel("Password")
                    Spacer(modifier = Modifier.height(8.dp))
                    ValidationTextField(
                        value = state.password,
                        onValueChange = viewModel::onPasswordChange,
                        placeholder = "Enter your password",
                        isPassword = true,
                        isError = state.passwordError != null,
                        errorMessage = state.passwordError,
                        imeAction = ImeAction.Done,
                        onImeAction = viewModel::login
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (state.isLoading) {
                        CircularProgressIndicator(
                            color = MunchlyColors.primary,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    } else {
                        AuthButton(
                            text = "Sign In",
                            onClick = viewModel::login,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    state.error?.let { error ->
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MunchlyColors.errorBackground
                            )
                        ) {
                            Text(
                                text = error,
                                color = MunchlyColors.error,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            SignUpPrompt(
                onNavigateToRegister = { navController.navigate("register_user_type") }
            )
        }
    }
}
