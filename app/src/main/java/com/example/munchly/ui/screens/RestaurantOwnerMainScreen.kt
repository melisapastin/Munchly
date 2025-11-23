package com.example.munchly.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.munchly.ui.components.BottomNavigationBar

@Composable
fun RestaurantOwnerMainScreen() {
    var currentScreen by rememberSaveable { mutableStateOf("home") }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentScreen = currentScreen,
                onHomeClick = { currentScreen = "home" },
                onProfileClick = { currentScreen = "profile" }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentScreen) {
                "home" -> RestaurantOwnerFeedScreen()
                "profile" -> RestaurantOwnerProfileScreen()
            }
        }
    }
}