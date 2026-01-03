package com.example.munchly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.munchly.ui.theme.MunchlyColors

/**
 * Component for uploading and managing restaurant menu PDF.
 * Shows upload button when no menu exists, and remove option when menu is uploaded.
 */
@Composable
fun MenuPdfUpload(
    menuPdfUrl: String?,
    onUploadClick: () -> Unit,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MunchlyColors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Menu PDF",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MunchlyColors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (menuPdfUrl.isNullOrEmpty()) {
                            "No menu uploaded"
                        } else {
                            "Menu uploaded"
                        },
                        fontSize = 12.sp,
                        color = MunchlyColors.textSecondary
                    )
                }

                if (menuPdfUrl.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MunchlyColors.primaryLight)
                            .clickable(onClick = onUploadClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Upload menu",
                            tint = MunchlyColors.primary
                        )
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = MunchlyColors.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        IconButton(onClick = onRemoveClick) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove menu",
                                tint = MunchlyColors.error
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "📄 Upload your menu in PDF format\n" +
                        "✓ Must include prices\n" +
                        "✓ Must include allergen information\n" +
                        "✓ Must include portion sizes",
                fontSize = 11.sp,
                color = MunchlyColors.textPlaceholder,
                lineHeight = 16.sp
            )
        }
    }
}