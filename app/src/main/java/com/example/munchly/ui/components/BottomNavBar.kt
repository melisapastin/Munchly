package com.example.munchly.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.munchly.ui.theme.MunchlyColors

/**
 * Bottom navigation destinations.
 */
enum class BottomNavDestination(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    FEED(
        label = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    PROFILE(
        label = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
}

/**
 * Bottom navigation bar component.
 * Provides navigation between main app sections.
 */
@Composable
fun BottomNavBar(
    currentDestination: BottomNavDestination,
    onDestinationSelected: (BottomNavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = MunchlyColors.surface,
        contentColor = MunchlyColors.textPrimary,
        modifier = modifier
    ) {
        BottomNavDestination.entries.forEach { destination ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (currentDestination == destination) {
                            destination.selectedIcon
                        } else {
                            destination.unselectedIcon
                        },
                        contentDescription = destination.label
                    )
                },
                label = {
                    Text(
                        text = destination.label,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                selected = currentDestination == destination,
                onClick = { onDestinationSelected(destination) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MunchlyColors.primary,
                    selectedTextColor = MunchlyColors.primary,
                    unselectedIconColor = MunchlyColors.textSecondary,
                    unselectedTextColor = MunchlyColors.textSecondary,
                    indicatorColor = MunchlyColors.primaryLight
                )
            )
        }
    }
}