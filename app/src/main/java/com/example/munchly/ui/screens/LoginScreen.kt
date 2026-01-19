package com.example.munchly.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.munchly.R
import com.example.munchly.ui.components.AppLogo
import com.example.munchly.ui.components.AppTitle
import com.example.munchly.ui.components.AuthButton
import com.example.munchly.ui.components.AuthFieldLabel
import com.example.munchly.ui.components.SignUpPrompt
import com.example.munchly.ui.components.ValidationTextField
import com.example.munchly.ui.theme.MunchlyColors
import com.example.munchly.ui.viewmodels.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
/**
 * Login screen for existing users.
 * Uses domain models (UserDomain) exclusively.
 */
@Composable
fun LoginScreen(navController: NavController) {
    val app = LocalContext.current.applicationContext as MunchlyApplication
    val context = LocalContext.current
    val viewModel = remember { LoginViewModel(
        app.loginUseCase,
        googleLoginUseCase = app.googleLoginUseCase
    ) }
    val state by viewModel.uiState.collectAsState()

    // 1. Set up the launcher to receive the result from Google
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken
            if (idToken != null) {
                viewModel.signInWithGoogle(idToken)
            } else {
                // Handle missing token
            }
        } catch (e: ApiException) {
            // Handle error (e.g., user cancelled)
        }
    }
    val startGoogleSignIn = {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val client = GoogleSignIn.getClient(context, gso)
        googleSignInLauncher.launch(client.signInIntent)
    }
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
                    // Inside your Column, below the "Sign In" AuthButton
                    // Place this inside your Column, below the main AuthButton/CircularProgressIndicator
                    Spacer(modifier = Modifier.height(16.dp))

                    androidx.compose.material3.TextButton(
                        onClick = startGoogleSignIn,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        androidx.compose.foundation.layout.Row(
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            // Optional: You can add an Icon here if you have a Google drawable
                            Text(
                                text = "Continue with Google",
                                color = MunchlyColors.primary, // Using your theme's brand color
                                fontSize = 16.sp,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
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

