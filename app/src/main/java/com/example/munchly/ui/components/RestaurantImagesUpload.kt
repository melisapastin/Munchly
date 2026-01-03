package com.example.munchly.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.munchly.ui.theme.MunchlyColors

/**
 * Component for uploading and managing restaurant images.
 * Supports up to 20 images with add/remove functionality.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RestaurantImagesUpload(
    images: List<String>,
    onAddImage: () -> Unit,
    onRemoveImage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Restaurant Images (${images.size}/20)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MunchlyColors.textPrimary
            )
            Text(
                text = "Max 20 images",
                fontSize = 12.sp,
                color = MunchlyColors.textSecondary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            images.forEach { imageUrl ->
                ImageThumbnail(
                    imageUrl = imageUrl,
                    onRemove = { onRemoveImage(imageUrl) }
                )
            }

            if (images.size < 20) {
                AddImageBox(onClick = onAddImage)
            }
        }

        if (images.isEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Add photos of your restaurant, dishes, ambiance, etc.",
                fontSize = 12.sp,
                color = MunchlyColors.textPlaceholder,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Individual image thumbnail with remove button.
 */
@Composable
private fun ImageThumbnail(
    imageUrl: String,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(100.dp)
            .clip(RoundedCornerShape(8.dp))
    ) {
        Image(
            painter = rememberAsyncImagePainter(imageUrl),
            contentDescription = "Restaurant image",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(24.dp)
                .background(
                    Color.Black.copy(alpha = 0.6f),
                    RoundedCornerShape(12.dp)
                )
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove image",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

/**
 * Box for adding new images.
 */
@Composable
private fun AddImageBox(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(100.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 2.dp,
                color = MunchlyColors.borderDefault,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .background(MunchlyColors.surface),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = "Add image",
                tint = MunchlyColors.textSecondary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Add",
                fontSize = 12.sp,
                color = MunchlyColors.textSecondary
            )
        }
    }
}