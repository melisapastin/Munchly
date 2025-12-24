package com.example.munchly.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.munchly.ui.theme.MunchlyColors

@Composable
fun SignInPrompt(onNavigateToLogin: () -> Unit) {
    Row {
        Text(
            text = "Already have an account? ",
            fontSize = 14.sp,
            color = MunchlyColors.textSecondary
        )
        Text(
            text = "Sign in",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MunchlyColors.primary,
            modifier = Modifier.clickable(onClick = onNavigateToLogin)
        )
    }
}