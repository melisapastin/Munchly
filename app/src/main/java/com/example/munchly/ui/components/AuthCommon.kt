package com.example.munchly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppLogo() {
    Box(
        modifier = Modifier
            .size(80.dp)
            .background(
                color = Color(0xFFD2691E),
                shape = RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "M",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun AppTitle(
    title: String,
    subtitle: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF8B4513)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            fontSize = 14.sp,
            color = Color(0xFF8B7355)
        )
    }
}

@Composable
fun ErrorMessage(error: String?) {
    error?.let {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = it,
            color = Color.Red,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

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