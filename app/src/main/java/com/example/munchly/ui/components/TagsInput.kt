package com.example.munchly.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.munchly.ui.theme.MunchlyColors

/**
 * Component for managing restaurant tags.
 * Displays existing tags with remove buttons and an add button.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagsInput(
    tags: List<String>,
    onTagAdd: () -> Unit,
    onTagRemove: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false
) {
    Column(modifier = modifier) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tags.forEach { tag ->
                TagChip(
                    text = tag,
                    onRemove = { onTagRemove(tag) }
                )
            }

            if (tags.size < 10) {
                AddTagChip(onClick = onTagAdd)
            }
        }
    }
}

/**
 * Individual tag chip with remove button.
 */
@Composable
private fun TagChip(
    text: String,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    AssistChip(
        onClick = { },
        label = { Text(text) },
        trailingIcon = {
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(18.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove tag",
                    modifier = Modifier.size(14.dp)
                )
            }
        },
        modifier = modifier,
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MunchlyColors.primaryLight,
            labelColor = MunchlyColors.primary
        )
    )
}

/**
 * Chip button for adding new tags.
 */
@Composable
private fun AddTagChip(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AssistChip(
        onClick = onClick,
        label = { Text("Add tag") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        },
        modifier = modifier,
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MunchlyColors.surface,
            labelColor = MunchlyColors.textSecondary
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MunchlyColors.borderDefault
        )
    )
}