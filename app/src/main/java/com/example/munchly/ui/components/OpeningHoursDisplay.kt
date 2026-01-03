package com.example.munchly.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.munchly.domain.models.DayOfWeek
import com.example.munchly.domain.models.DayScheduleDomain
import com.example.munchly.ui.theme.MunchlyColors

/**
 * Displays restaurant opening hours in a read-only format.
 * Used in profile display mode.
 */
@Composable
fun OpeningHoursDisplay(
    schedule: Map<String, DayScheduleDomain>,
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
            DayOfWeek.entries.forEach { day ->
                val daySchedule = schedule[day.name]

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = day.displayName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MunchlyColors.textPrimary
                    )

                    Text(
                        text = daySchedule?.toDisplayString() ?: "Closed",
                        fontSize = 14.sp,
                        color = if (daySchedule?.isOpen == true) {
                            MunchlyColors.textPrimary
                        } else {
                            MunchlyColors.textSecondary
                        }
                    )
                }
            }
        }
    }
}

/**
 * Extension function to format day schedule for display.
 */
private fun DayScheduleDomain.toDisplayString(): String {
    return if (isOpen) {
        "$openTime - $closeTime"
    } else {
        "Closed"
    }
}