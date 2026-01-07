package com.example.munchly.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.munchly.ui.theme.MunchlyColors
import com.example.munchly.ui.viewmodels.OwnerFeedViewModel

/**
 * FIXED: Now displays calculated average rating instead of stats average
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerFeedScreen(
    viewModel: OwnerFeedViewModel,
    onNavigateToRatings: () -> Unit,
    onNavigateToReviews: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Dashboard",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MunchlyColors.surface,
                    titleContentColor = MunchlyColors.textPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MunchlyColors.background)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MunchlyColors.primary
                    )
                }

                !state.hasRestaurant -> {
                    EmptyRestaurantState(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                state.error != null -> {
                    ErrorState(
                        message = state.error ?: "An error occurred",
                        onRetry = { viewModel.loadRestaurantData() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    RestaurantDashboard(
                        state = state,
                        onNavigateToRatings = onNavigateToRatings,
                        onNavigateToReviews = onNavigateToReviews
                    )
                }
            }
        }
    }
}

/**
 * FIXED: Now uses calculated values from state
 */
@Composable
private fun RestaurantDashboard(
    state: com.example.munchly.ui.viewmodels.OwnerFeedState,
    onNavigateToRatings: () -> Unit,
    onNavigateToReviews: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            RestaurantHeader(
                restaurantName = state.restaurant?.name ?: "My Restaurant",
                restaurantDescription = state.restaurant?.description ?: ""
            )
        }

        item {
            QuickStatsOverview(
                averageRating = state.calculatedAverageRating,
                totalRatings = state.calculatedTotalRatings,
                totalReviews = state.calculatedTotalReviews,
                totalBookmarks = state.actualBookmarkCount,  // FIXED: Use real count from database
                monthlyViews = state.stats?.monthlyViews ?: 0,
                onRatingsClick = onNavigateToRatings,
                onReviewsClick = onNavigateToReviews
            )
        }

        /*
        if (state.recentReviews.isNotEmpty()) {
            item {
                RecentActivitySummary(
                    recentReviewsCount = state.recentReviews.size,
                    onViewAllClick = onNavigateToReviews
                )
            }
        }
         */
    }
}

@Composable
private fun RestaurantHeader(
    restaurantName: String,
    restaurantDescription: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MunchlyColors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MunchlyColors.primary.copy(alpha = 0.1f),
                            MunchlyColors.surface
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MunchlyColors.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Restaurant,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = restaurantName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MunchlyColors.textPrimary
                        )
                        Text(
                            text = "Your Restaurant",
                            fontSize = 14.sp,
                            color = MunchlyColors.textSecondary
                        )
                    }
                }

                if (restaurantDescription.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = restaurantDescription,
                        fontSize = 14.sp,
                        color = MunchlyColors.textSecondary,
                        lineHeight = 20.sp,
                        maxLines = 2
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickStatsOverview(
    averageRating: Double,
    totalRatings: Int,
    totalReviews: Int,
    totalBookmarks: Int,
    monthlyViews: Int,
    onRatingsClick: () -> Unit,
    onReviewsClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MunchlyColors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Performance Overview",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MunchlyColors.textPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatItem(
                    icon = Icons.Default.Star,
                    value = if (averageRating > 0) String.format("%.1f", averageRating) else "N/A",
                    label = "$totalRatings ratings",
                    color = Color(0xFFFFA726),
                    onClick = onRatingsClick,
                    modifier = Modifier.weight(1f)
                )

                StatItem(
                    icon = Icons.Default.RateReview,
                    value = totalReviews.toString(),
                    label = "reviews",
                    color = Color(0xFF66BB6A),
                    onClick = onReviewsClick,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatItem(
                    icon = Icons.Default.Bookmark,
                    value = totalBookmarks.toString(),
                    label = "bookmarks",
                    color = Color(0xFF42A5F5),
                    onClick = {},
                    modifier = Modifier.weight(1f)
                )

                StatItem(
                    icon = Icons.Default.Visibility,
                    value = monthlyViews.toString(),
                    label = "monthly views",
                    color = Color(0xFFAB47BC),
                    onClick = {},
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    icon: ImageVector,
    value: String,
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MunchlyColors.textPrimary
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = MunchlyColors.textSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

/*
@Composable
private fun RecentActivitySummary(
    recentReviewsCount: Int,
    onViewAllClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onViewAllClick),
        colors = CardDefaults.cardColors(containerColor = MunchlyColors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF66BB6A).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = Color(0xFF66BB6A),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Recent Activity",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MunchlyColors.textPrimary
                )
                Text(
                    text = "You have $recentReviewsCount new review${if (recentReviewsCount != 1) "s" else ""}",
                    fontSize = 13.sp,
                    color = MunchlyColors.textSecondary
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = MunchlyColors.textSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
 */

@Composable
private fun EmptyRestaurantState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Restaurant,
            contentDescription = null,
            tint = MunchlyColors.textSecondary,
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Restaurant Yet",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MunchlyColors.textPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Set up your restaurant profile to start receiving reviews and tracking statistics",
            fontSize = 14.sp,
            color = MunchlyColors.textSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = MunchlyColors.error,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            fontSize = 14.sp,
            color = MunchlyColors.error,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = MunchlyColors.primary
            )
        ) {
            Text("Retry")
        }
    }
}