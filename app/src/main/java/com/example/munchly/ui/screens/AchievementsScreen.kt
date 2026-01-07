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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.munchly.ui.components.AchievementCard
import com.example.munchly.ui.components.ErrorState
import com.example.munchly.ui.components.LoadingState
import com.example.munchly.ui.theme.MunchlyColors
import com.example.munchly.ui.viewmodels.AchievementsViewModel

/**
 * Screen displaying user's achievements and progress.
 * Shows both earned and in-progress achievements with progress bars.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    viewModel: AchievementsViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Achievements",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MunchlyColors.textPrimary
                        )
                    }
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
                    LoadingState()
                }

                state.error != null -> {
                    ErrorState(
                        message = state.error ?: "An error occurred",
                        onRetry = { viewModel.loadAchievements() }
                    )
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Summary Card
                        item {
                            AchievementSummaryCard(
                                earnedCount = state.earnedAchievements.size,
                                totalCount = state.allAchievements.size
                            )
                        }

                        // Achievements List
                        items(
                            items = state.allAchievements,
                            key = { it.achievementType.name }
                        ) { achievement ->
                            AchievementCard(achievement = achievement)
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

/**
 * Summary card showing achievement progress overview.
 */
@Composable
private fun AchievementSummaryCard(
    earnedCount: Int,
    totalCount: Int
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = MunchlyColors.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = "$earnedCount / $totalCount",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = MunchlyColors.textPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Achievements Earned",
                fontSize = 14.sp,
                color = MunchlyColors.textSecondary,
                textAlign = TextAlign.Center
            )

            if (earnedCount < totalCount) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${totalCount - earnedCount} more to go!",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MunchlyColors.primary
                )
            }
        }
    }
}