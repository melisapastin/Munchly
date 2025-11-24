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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.munchly.data.models.UserType
import com.example.munchly.data.remote.RegisterRemoteDataSourceImpl
import com.example.munchly.data.repository.RegisterRepositoryImpl
import com.example.munchly.domain.usecases.RegisterUseCase
import com.example.munchly.ui.components.AppLogo
import com.example.munchly.ui.components.AppTitle
import com.example.munchly.ui.components.AuthButton
import com.example.munchly.ui.components.AuthFieldLabel
import com.example.munchly.ui.components.SignInPrompt
import com.example.munchly.ui.components.ValidationTextField
import com.example.munchly.ui.viewmodels.RegisterCredentialsViewModel
import com.example.munchly.ui.viewmodels.RegisterCredentialsViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.munchly.ui.viewmodels.RegisterCredentialsState

@Composable
fun RegisterCredentialsScreen(
    navController: NavController,
    userType: UserType
) {
    // Create dependencies inside the composable
    val registerUseCase = remember {
        val remoteDataSource = RegisterRemoteDataSourceImpl(
            auth = FirebaseAuth.getInstance(),
            firestore = FirebaseFirestore.getInstance()
        )
        val repository = RegisterRepositoryImpl(remoteDataSource)
        RegisterUseCase(repository)
    }

    val viewModel: RegisterCredentialsViewModel = viewModel(
        factory = RegisterCredentialsViewModelFactory(registerUseCase)
    )
    val state by viewModel.uiState.collectAsState()

    // Handle navigation on success
    LaunchedEffect(state.registrationSuccess) {
        if (state.registrationSuccess) {
            navController.navigate("home") // Simple success navigation
        }
    }

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
                title = "Create Account",
                subtitle = "Join as ${if (userType == UserType.RESTAURANT_OWNER) "Restaurant Owner" else "Food Lover"}"
            )
            Spacer(modifier = Modifier.height(32.dp))

            CredentialsForm(
                state = state,
                onUsernameChange = viewModel::onUsernameChange,
                onEmailChange = viewModel::onEmailChange,
                onPasswordChange = viewModel::onPasswordChange,
                onRegister = { viewModel.register(userType) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            SignInPrompt(
                onNavigateToLogin = { navController.navigate("login") }
            )

            state.error?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun CredentialsForm(
    state: RegisterCredentialsState,
    onUsernameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRegister: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        AuthFieldLabel("Username")
        Spacer(modifier = Modifier.height(8.dp))
        ValidationTextField(
            value = state.username,
            onValueChange = onUsernameChange,
            placeholder = "Choose a username",
            isError = state.usernameError != null,
            errorMessage = state.usernameError
        )

        Spacer(modifier = Modifier.height(16.dp))

        AuthFieldLabel("Email")
        Spacer(modifier = Modifier.height(8.dp))
        ValidationTextField(
            value = state.email,
            onValueChange = onEmailChange,
            placeholder = "your@email.com",
            isError = state.emailError != null,
            errorMessage = state.emailError
        )

        Spacer(modifier = Modifier.height(16.dp))

        AuthFieldLabel("Password")
        Spacer(modifier = Modifier.height(8.dp))
        ValidationTextField(
            value = state.password,
            onValueChange = onPasswordChange,
            placeholder = "········",
            isPassword = true,
            isError = state.passwordError != null,
            errorMessage = state.passwordError
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (state.isLoading) {
            CircularProgressIndicator(
                color = Color(0xFFD2691E),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            AuthButton(
                text = "Create Account",
                onClick = onRegister,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}