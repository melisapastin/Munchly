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
import com.example.munchly.data.models.UserType
import com.example.munchly.ui.components.AppLogo
import com.example.munchly.ui.components.AppTitle
import com.example.munchly.ui.components.AuthButton
import com.example.munchly.ui.components.ErrorMessage
import com.example.munchly.ui.components.SignInPrompt
import com.example.munchly.ui.components.UserTypeCard
import com.example.munchly.ui.viewmodels.RegisterUiState
import com.example.munchly.ui.viewmodels.RegisterViewModel
import com.example.munchly.ui.viewmodels.RegisterViewModelFactory

@Composable
fun RegisterUserTypeScreen(
    navController: NavController
) {
    // Clean ViewModel acquisition with factory pattern
    val viewModel: RegisterViewModel = viewModel(factory = RegisterViewModelFactory())
    val uiState by viewModel.uiState.collectAsState()

    RegisterUserTypeScreenContent(
        uiState = uiState,
        onUserTypeSelected = viewModel::onUserTypeSelected,
        onContinue = {
            val userType = uiState.selectedUserType ?: return@RegisterUserTypeScreenContent
            android.util.Log.d("RegisterUserTypeScreen", "Continuing with userType: $userType")
            navController.navigate("register_credentials?userType=${userType.name}")
        },
        onNavigateToLogin = { navController.navigate("login") }
    )
}

@Composable
private fun RegisterUserTypeScreenContent(
    uiState: RegisterUiState,
    onUserTypeSelected: (UserType) -> Unit,
    onContinue: () -> Unit,
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
                subtitle = "Join Us Today"
            )
            Spacer(modifier = Modifier.height(32.dp))

            UserTypeSelectionSection(
                selectedUserType = uiState.selectedUserType,
                onUserTypeSelected = onUserTypeSelected
            )

            Spacer(modifier = Modifier.height(32.dp))

            ContinueButton(
                isLoading = uiState.isLoading,
                isEnabled = uiState.selectedUserType != null,
                onContinue = onContinue
            )

            Spacer(modifier = Modifier.height(24.dp))

            SignInPrompt(onNavigateToLogin)

            ErrorMessage(uiState.error)
        }
    }
}

@Composable
private fun UserTypeSelectionSection(
    selectedUserType: UserType?,
    onUserTypeSelected: (UserType) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Section header
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

        // User type options
        UserTypeCard(
            title = "Food Lover",
            description = "Discover and bookmark restaurants",
            isSelected = selectedUserType == UserType.FOOD_LOVER,
            onClick = { onUserTypeSelected(UserType.FOOD_LOVER) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        UserTypeCard(
            title = "Restaurant Owner",
            description = "List and manage your restaurant",
            isSelected = selectedUserType == UserType.RESTAURANT_OWNER,
            onClick = { onUserTypeSelected(UserType.RESTAURANT_OWNER) }
        )
    }
}

@Composable
private fun ContinueButton(
    isLoading: Boolean,
    isEnabled: Boolean,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = Color(0xFFD2691E))
        } else {
            AuthButton(
                text = "Continue",
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth(),
                isEnabled = isEnabled
            )
        }
    }
}