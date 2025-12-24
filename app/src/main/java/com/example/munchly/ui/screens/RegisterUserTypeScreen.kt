package com.example.munchly.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.munchly.data.models.UserType
import com.example.munchly.ui.components.AppLogo
import com.example.munchly.ui.components.AppTitle
import com.example.munchly.ui.components.AuthButton
import com.example.munchly.ui.components.SignInPrompt
import com.example.munchly.ui.components.UserTypeCard
import com.example.munchly.ui.theme.MunchlyColors
import com.example.munchly.ui.viewmodels.RegisterUserTypeViewModel

@Composable
fun RegisterUserTypeScreen(
    navController: NavController
) {
    val viewModel: RegisterUserTypeViewModel = viewModel()
    val state by viewModel.uiState.collectAsState()

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
                title = "Join Munchly",
                subtitle = "Choose how you'll use our platform"
            )
            Spacer(modifier = Modifier.height(32.dp))

            UserTypeSelectionSection(
                selectedUserType = state.selectedUserType,
                onUserTypeSelected = viewModel::onUserTypeSelected
            )

            Spacer(modifier = Modifier.height(32.dp))

            AuthButton(
                text = "Continue",
                onClick = {
                    val userType = state.selectedUserType ?: return@AuthButton
                    navController.navigate("register_credentials/${userType.name}")
                },
                modifier = Modifier.fillMaxWidth(),
                isEnabled = state.selectedUserType != null
            )

            Spacer(modifier = Modifier.height(24.dp))

            SignInPrompt(
                onNavigateToLogin = { navController.navigate("login") }
            )
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
        UserTypeCard(
            title = "Food Lover",
            description = "Discover and bookmark amazing restaurants",
            isSelected = selectedUserType == UserType.FOOD_LOVER,
            onClick = { onUserTypeSelected(UserType.FOOD_LOVER) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        UserTypeCard(
            title = "Restaurant Owner",
            description = "Showcase and manage your restaurant",
            isSelected = selectedUserType == UserType.RESTAURANT_OWNER,
            onClick = { onUserTypeSelected(UserType.RESTAURANT_OWNER) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}