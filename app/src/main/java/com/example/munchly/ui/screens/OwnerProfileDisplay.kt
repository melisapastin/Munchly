package com.example.munchly.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.munchly.ui.components.OpeningHoursDisplay
import com.example.munchly.ui.theme.MunchlyColors
import com.example.munchly.ui.viewmodels.OwnerProfileViewModel

/**
 * Display mode for restaurant owner profile.
 * Shows read-only view of restaurant information.
 */
@Composable
fun OwnerProfileDisplay(
    state: com.example.munchly.ui.viewmodels.OwnerProfileState,
    viewModel: OwnerProfileViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MunchlyColors.background)
    ) {
        ProfileHeader(
            restaurantName = state.restaurant?.name ?: "",
            username = viewModel.username
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Restaurant Information",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MunchlyColors.textPrimary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                InfoCard(
                    label = "Description",
                    value = state.restaurant?.description ?: ""
                )
            }

            item {
                TagsDisplayCard(tags = state.restaurant?.tags ?: emptyList())
            }

            item {
                InfoCard(
                    label = "Price Range",
                    value = "${state.restaurant?.priceRange?.symbol} - ${state.restaurant?.priceRange?.getDisplayDescription()}"
                )
            }

            item {
                Text(
                    text = "Contact Details",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MunchlyColors.textPrimary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                InfoCard(
                    label = "Address",
                    value = state.restaurant?.address ?: ""
                )
            }

            item {
                InfoCard(
                    label = "Phone",
                    value = state.restaurant?.phone ?: ""
                )
            }

            item {
                Text(
                    text = "Business Hours",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MunchlyColors.textPrimary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                val schedule = state.restaurant?.openingHours ?: emptyMap()
                OpeningHoursDisplay(schedule = schedule)
            }

            if (!state.restaurant?.menuPdfUrl.isNullOrEmpty() ||
                state.restaurant?.images?.isNotEmpty() == true
            ) {
                item {
                    Text(
                        text = "Media",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MunchlyColors.textPrimary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            if (!state.restaurant?.menuPdfUrl.isNullOrEmpty()) {
                item {
                    InfoCard(
                        label = "Menu",
                        value = "Menu PDF uploaded ✓"
                    )
                }
            }

            if (state.restaurant?.images?.isNotEmpty() == true) {
                item {
                    InfoCard(
                        label = "Images",
                        value = "${state.restaurant.images.size} photos uploaded"
                    )
                }
            }
        }
    }
}

/**
 * Profile header with restaurant icon and name.
 */
@Composable
private fun ProfileHeader(
    restaurantName: String,
    username: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MunchlyColors.primary.copy(alpha = 0.15f),
                        MunchlyColors.background
                    )
                )
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MunchlyColors.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Restaurant,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = restaurantName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MunchlyColors.textPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "@$username",
                fontSize = 14.sp,
                color = MunchlyColors.textSecondary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Generic info card for displaying label-value pairs.
 */
@Composable
private fun InfoCard(label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MunchlyColors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MunchlyColors.textSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 15.sp,
                color = MunchlyColors.textPrimary,
                lineHeight = 22.sp
            )
        }
    }
}

/**
 * Card for displaying restaurant tags.
 */
@Composable
private fun TagsDisplayCard(tags: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MunchlyColors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Tags",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MunchlyColors.textSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = tags.joinToString(", "),
                fontSize = 15.sp,
                color = MunchlyColors.textPrimary,
                lineHeight = 22.sp
            )
        }
    }
}

/**
 * Extension function for price range display description.
 * This is UI-level presentation logic.
 */
private fun com.example.munchly.domain.models.PriceRangeDomain.getDisplayDescription(): String {
    return when (this) {
        com.example.munchly.domain.models.PriceRangeDomain.BUDGET -> "Budget-friendly"
        com.example.munchly.domain.models.PriceRangeDomain.MEDIUM -> "Moderate"
        com.example.munchly.domain.models.PriceRangeDomain.EXPENSIVE -> "Fine dining"
    }
}