package com.example.munchly.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

enum class BottomNavDestination {
    FEED, PROFILE
}

@Composable
fun BottomNavBar(
    currentDestination: BottomNavDestination,
    onDestinationSelected: (BottomNavDestination) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Feed"
                )
            },
            label = { Text("Home") },
            selected = currentDestination == BottomNavDestination.FEED,
            onClick = { onDestinationSelected(BottomNavDestination.FEED) }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile"
                )
            },
            label = { Text("Profile") },
            selected = currentDestination == BottomNavDestination.PROFILE,
            onClick = { onDestinationSelected(BottomNavDestination.PROFILE) }
        )
    }
}