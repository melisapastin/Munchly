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
import com.example.munchly.data.models.UserType
import com.example.munchly.ui.components.AppLogo
import com.example.munchly.ui.components.AppTitle
import com.example.munchly.ui.components.AuthButton
import com.example.munchly.ui.components.AuthFieldLabel
import com.example.munchly.ui.components.SignInPrompt
import com.example.munchly.ui.components.ValidationTextField
import com.example.munchly.ui.theme.MunchlyColors
import com.example.munchly.ui.viewmodels.RegisterCredentialsViewModel

@Composable
fun RegisterCredentialsScreen(
    navController: NavController,
    userType: UserType
) {
    val app = LocalContext.current.applicationContext as MunchlyApplication
    val viewModel = remember { RegisterCredentialsViewModel(app.registerUseCase) }
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.registrationSuccess) {
        if (state.registrationSuccess) {
            state.user?.let { user ->
                navController.navigate("main/${user.userType.name}/${user.username}") {
                    popUpTo("register_user_type") { inclusive = true }
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
                title = "Create Account",
                subtitle = "Join as ${if (userType == UserType.RESTAURANT_OWNER) "Restaurant Owner" else "Food Lover"}"
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
                    AuthFieldLabel("Username")
                    Spacer(modifier = Modifier.height(8.dp))
                    ValidationTextField(
                        value = state.username,
                        onValueChange = viewModel::onUsernameChange,
                        placeholder = "Choose a username",
                        isError = state.usernameError != null,
                        errorMessage = state.usernameError,
                        imeAction = ImeAction.Next
                    )

                    Spacer(modifier = Modifier.height(16.dp))

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
                        placeholder = "········",
                        isPassword = true,
                        isError = state.passwordError != null,
                        errorMessage = state.passwordError,
                        imeAction = ImeAction.Done,
                        onImeAction = { viewModel.register(userType) }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (state.isLoading) {
                        CircularProgressIndicator(
                            color = MunchlyColors.primary,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    } else {
                        AuthButton(
                            text = "Create Account",
                            onClick = { viewModel.register(userType) },
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

            SignInPrompt(
                onNavigateToLogin = { navController.navigate("login") }
            )
        }
    }
}