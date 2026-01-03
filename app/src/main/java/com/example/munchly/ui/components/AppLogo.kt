package com.example.munchly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.munchly.ui.theme.MunchlyColors

@Composable
fun AppLogo() {
    Box(
        modifier = Modifier
            .size(80.dp)
            .shadow(8.dp, RoundedCornerShape(20.dp))
            .background(
                color = MunchlyColors.primary,
                shape = RoundedCornerShape(20.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Restaurant,
            contentDescription = "Munchly Logo",
            tint = Color.White,
            modifier = Modifier.size(40.dp)
        )
    }
}