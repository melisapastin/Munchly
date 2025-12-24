package com.example.munchly.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.material3.Scaffold
import com.example.munchly.data.models.UserType
import com.example.munchly.ui.components.BottomNavBar
import com.example.munchly.ui.components.BottomNavDestination

@Composable
fun MainScreen(
    userType: UserType,
    username: String
) {
    val currentDestination = remember { mutableStateOf(BottomNavDestination.FEED) }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentDestination = currentDestination.value,
                onDestinationSelected = { destination ->
                    currentDestination.value = destination
                }
            )
        }
    ) { innerPadding ->
        when (currentDestination.value) {
            BottomNavDestination.FEED -> FeedScreen(
                userType = userType,
                modifier = Modifier.padding(innerPadding)
            )
            BottomNavDestination.PROFILE -> ProfileScreen(
                username = username,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}