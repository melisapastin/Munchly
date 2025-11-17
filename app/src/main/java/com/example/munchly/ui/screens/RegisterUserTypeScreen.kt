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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.munchly.data.models.UserType
import com.example.munchly.ui.components.AuthButton
import com.example.munchly.ui.components.UserTypeCard
import com.example.munchly.ui.viewmodels.RegisterViewModel

@Composable
fun RegisterUserTypeScreen(
    navController: NavController,
    viewModel: RegisterViewModel
) {
    val uiState = viewModel.uiState.value

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
                text = "Join Us Today",
                fontSize = 14.sp,
                color = Color(0xFF8B7355)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // "I am a..." Text
            Text(
                text = "I am a...",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF8B4513)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Select how you'll use Munchly",
                fontSize = 14.sp,
                color = Color(0xFF8B7355)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Food Lover Card
            UserTypeCard(
                title = "Food Lover",
                description = "Discover and bookmark restaurants",
                isSelected = uiState.selectedUserType == UserType.FOOD_LOVER,
                onClick = { viewModel.onUserTypeSelected(UserType.FOOD_LOVER) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Restaurant Owner Card
            UserTypeCard(
                title = "Restaurant Owner",
                description = "List and manage your restaurant",
                isSelected = uiState.selectedUserType == UserType.RESTAURANT_OWNER,
                onClick = { viewModel.onUserTypeSelected(UserType.RESTAURANT_OWNER) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Continue Button
            if (uiState.isLoading) {
                CircularProgressIndicator(color = Color(0xFFD2691E))
            } else {
                AuthButton(
                    text = "Continue",
                    onClick = {
                        // Store the selected type and navigate
                        viewModel.onUserTypeSelected(uiState.selectedUserType!!)
                        navController.navigate("register_credentials")
                    },
                    isEnabled = uiState.selectedUserType != null
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