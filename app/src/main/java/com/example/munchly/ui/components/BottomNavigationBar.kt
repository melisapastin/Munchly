package com.example.munchly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomNavigationBar(
    currentScreen: String,
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(Color(0xFFD2691E))
            .padding(horizontal = 32.dp),
        horizontalArrangement = Arrangement.Center, // Changed to center
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Home Button
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1f) // Equal weight for both buttons
        ) {
            IconButton(
                onClick = onHomeClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    painter = painterResource(android.R.drawable.ic_menu_view),
                    contentDescription = "Home",
                    tint = if (currentScreen == "home") Color.White else Color(0xFFE8D5C4)
                )
            }
            Text(
                text = "Feed",
                fontSize = 12.sp,
                color = if (currentScreen == "home") Color.White else Color(0xFFE8D5C4)
            )
        }

        // Profile Button
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1f) // Equal weight for both buttons
        ) {
            IconButton(
                onClick = onProfileClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    painter = painterResource(android.R.drawable.ic_menu_my_calendar),
                    contentDescription = "Profile",
                    tint = if (currentScreen == "profile") Color.White else Color(0xFFE8D5C4)
                )
            }
            Text(
                text = "Profile",
                fontSize = 12.sp,
                color = if (currentScreen == "profile") Color.White else Color(0xFFE8D5C4)
            )
        }
    }
}