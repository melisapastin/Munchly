package com.example.munchly.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.example.munchly.data.models.UserType

@Composable
fun FeedScreen(
    userType: UserType,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = when (userType) {
                UserType.FOOD_LOVER -> "There are no restaurants yet"
                UserType.RESTAURANT_OWNER -> "There are no stats for you yet"
            },
            fontSize = 16.sp,
            color = Color.Gray
        )
    }
}