package com.example.munchly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.munchly.domain.models.DayOfWeek
import com.example.munchly.domain.models.DayScheduleDomain
import com.example.munchly.ui.theme.MunchlyColors

/**
 * Component for editing restaurant opening hours.
 * Allows setting open/close times for each day of the week.
 */
@Composable
fun OpeningHoursSelector(
    schedule: Map<String, DayScheduleDomain>,
    onScheduleChange: (String, DayScheduleDomain) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DayOfWeek.entries.forEach { day ->
            val daySchedule = schedule[day.name] ?: DayScheduleDomain(
                isOpen = false,
                openTime = "09:00",
                closeTime = "22:00"
            )

            DayScheduleRow(
                day = day,
                schedule = daySchedule,
                onScheduleChange = { newSchedule ->
                    onScheduleChange(day.name, newSchedule)
                }
            )
        }
    }
}

/**
 * Individual row for a single day's schedule.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DayScheduleRow(
    day: DayOfWeek,
    schedule: DayScheduleDomain,
    onScheduleChange: (DayScheduleDomain) -> Unit,
    modifier: Modifier = Modifier
) {
    var showOpenTimePicker by remember { mutableStateOf(false) }
    var showCloseTimePicker by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MunchlyColors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = schedule.isOpen,
                onCheckedChange = { isOpen ->
                    onScheduleChange(schedule.copy(isOpen = isOpen))
                }
            )

            Text(
                text = day.displayName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MunchlyColors.textPrimary,
                modifier = Modifier.width(100.dp)
            )

            if (schedule.isOpen) {
                Spacer(modifier = Modifier.width(8.dp))

                TimeSelector(
                    label = "Open",
                    time = schedule.openTime,
                    onClick = { showOpenTimePicker = true },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                TimeSelector(
                    label = "Close",
                    time = schedule.closeTime,
                    onClick = { showCloseTimePicker = true },
                    modifier = Modifier.weight(1f)
                )
            } else {
                Text(
                    text = "Closed",
                    fontSize = 14.sp,
                    color = MunchlyColors.textSecondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    if (showOpenTimePicker) {
        val timeParts = schedule.openTime.split(":")
        val initialHour = timeParts.getOrNull(0)?.toIntOrNull() ?: 9
        val initialMinute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0

        TimePickerDialog(
            initialHour = initialHour,
            initialMinute = initialMinute,
            onDismiss = { showOpenTimePicker = false },
            onConfirm = { hour, minute ->
                val timeString = String.format("%02d:%02d", hour, minute)
                onScheduleChange(schedule.copy(openTime = timeString))
                showOpenTimePicker = false
            }
        )
    }

    if (showCloseTimePicker) {
        val timeParts = schedule.closeTime.split(":")
        val initialHour = timeParts.getOrNull(0)?.toIntOrNull() ?: 22
        val initialMinute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0

        TimePickerDialog(
            initialHour = initialHour,
            initialMinute = initialMinute,
            onDismiss = { showCloseTimePicker = false },
            onConfirm = { hour, minute ->
                val timeString = String.format("%02d:%02d", hour, minute)
                onScheduleChange(schedule.copy(closeTime = timeString))
                showCloseTimePicker = false
            }
        )
    }
}

/**
 * Time selector component showing current time with click handler.
 */
@Composable
private fun TimeSelector(
    label: String,
    time: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = MunchlyColors.textSecondary
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MunchlyColors.background,
                    RoundedCornerShape(6.dp)
                )
                .clickable(onClick = onClick)
                .padding(8.dp)
        ) {
            Text(
                text = time,
                fontSize = 12.sp,
                color = MunchlyColors.textPrimary
            )
        }
    }
}