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
import com.example.munchly.data.remote.LoginRemoteDataSourceImpl
import com.example.munchly.data.repository.LoginRepositoryImpl
import com.example.munchly.domain.usecases.LoginUseCase
import com.example.munchly.ui.components.AppLogo
import com.example.munchly.ui.components.AppTitle
import com.example.munchly.ui.components.AuthButton
import com.example.munchly.ui.components.AuthFieldLabel
import com.example.munchly.ui.components.SignUpPrompt
import com.example.munchly.ui.components.ValidationTextField
import com.example.munchly.ui.viewmodels.LoginState
import com.example.munchly.ui.viewmodels.LoginViewModel
import com.example.munchly.ui.viewmodels.LoginViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun LoginScreen(
    navController: NavController
) {
    // Create dependencies
    val loginUseCase = remember {
        val remoteDataSource = LoginRemoteDataSourceImpl(
            auth = FirebaseAuth.getInstance(),
            firestore = FirebaseFirestore.getInstance()
        )
        val repository = LoginRepositoryImpl(remoteDataSource)
        LoginUseCase(repository)
    }

    val viewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(loginUseCase)
    )
    val state by viewModel.uiState.collectAsState()

    // Handle navigation on success
    LaunchedEffect(state.loginSuccess) {
        if (state.loginSuccess) {
            state.user?.let { user ->
                navController.navigate("main/${user.userType.name}/${user.username}") {
                    popUpTo("login") { inclusive = true }
                }
            }
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
                title = "Welcome Back",
                subtitle = "Sign in to your account"
            )
            Spacer(modifier = Modifier.height(32.dp))

            LoginForm(
                state = state,
                onEmailChange = viewModel::onEmailChange,
                onPasswordChange = viewModel::onPasswordChange,
                onLogin = viewModel::login
            )

            Spacer(modifier = Modifier.height(24.dp))

            SignUpPrompt(
                onNavigateToRegister = { navController.navigate("register_user_type") }
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
private fun LoginForm(
    state: LoginState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
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
                text = "Sign In",
                onClick = onLogin,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}