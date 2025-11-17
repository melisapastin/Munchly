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
import com.example.munchly.ui.viewmodels.RegisterViewModel

@Composable
fun RegisterCredentialsScreen(
    navController: NavController,
    viewModel: RegisterViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    // Handle successful registration
    LaunchedEffect(uiState.registrationSuccess) {
        if (uiState.registrationSuccess) {
            navController.navigate("home") {
                popUpTo("register_credentials") { inclusive = true }
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
                .padding(16.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(color = Color(0xFFD2691E), shape = RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) { }

            Spacer(modifier = Modifier.height(16.dp))

            // App Name
            Text(
                text = "Munchly",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8B4513)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Tagline
            Text(
                text = "Create Your Account",
                fontSize = 14.sp,
                color = Color(0xFF8B7355)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Username Section
            AuthFieldLabel("Username")
            Spacer(modifier = Modifier.height(8.dp))
            AuthTextField(
                value = uiState.username,
                onValueChange = viewModel::onUsernameChange,
                placeholder = "Choose a username"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email Section
            AuthFieldLabel("Email")
            Spacer(modifier = Modifier.height(8.dp))
            AuthTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                placeholder = "your@email.com"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Section
            AuthFieldLabel("Password")
            Spacer(modifier = Modifier.height(8.dp))
            AuthTextField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                placeholder = "········",
                isPassword = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Sign Up Button
            if (uiState.isLoading) {
                CircularProgressIndicator(color = Color(0xFFD2691E))
            } else {
                AuthButton(
                    text = "Sign Up",
                    onClick = viewModel::register
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sign In Text
            Row {
                Text(
                    text = "Already have an account? ",
                    fontSize = 14.sp,
                    color = Color(0xFF8B7355)
                )
                Text(
                    text = "Sign in",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD2691E),
                    modifier = Modifier.clickable { navController.navigate("login") }
                )
            }

            // Error Message
            uiState.error?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = error, color = Color.Red, fontSize = 14.sp)
            }
        }
    }
}