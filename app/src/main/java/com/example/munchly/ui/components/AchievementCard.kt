package com.example.munchly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.munchly.domain.models.AchievementDomain
import com.example.munchly.ui.theme.MunchlyColors

/**
 * Card component for displaying achievement with progress.
 * Shows icon, title, description, and progress bar.
 */
@Composable
fun AchievementCard(
    achievement: AchievementDomain,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.isCompleted) {
                MunchlyColors.primaryLight
            } else {
                MunchlyColors.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Achievement Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        if (achievement.isCompleted) {
                            MunchlyColors.primary
                        } else {
                            MunchlyColors.surface
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = achievement.achievementType.icon,
                    fontSize = 28.sp
                )
            }

            Spacer(modifier = Modifier.size(16.dp))

            // Achievement Details
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = achievement.achievementType.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MunchlyColors.textPrimary
                    )

                    if (achievement.isCompleted) {
                        Text(
                            text = "✓",
                            fontSize = 20.sp,
                            color = MunchlyColors.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = achievement.achievementType.description,
                    fontSize = 13.sp,
                    color = MunchlyColors.textSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Progress Bar
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Progress",
                            fontSize = 11.sp,
                            color = MunchlyColors.textSecondary
                        )
                        Text(
                            text = "${achievement.progress}/${achievement.requirement}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MunchlyColors.textPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    LinearProgressIndicator(
                        progress = achievement.completionPercentage / 100f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (achievement.isCompleted) {
                            MunchlyColors.primary
                        } else {
                            MunchlyColors.primary.copy(alpha = 0.5f)
                        },
                        trackColor = MunchlyColors.borderDefault
                    )
                }
            }
        }
    }
}