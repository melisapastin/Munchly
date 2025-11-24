package com.example.munchly.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun SignInPrompt(onNavigateToLogin: () -> Unit) {
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
            modifier = Modifier.clickable(onClick = onNavigateToLogin)
        )
    }
}