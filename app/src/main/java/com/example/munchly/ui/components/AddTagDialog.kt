package com.example.munchly.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.munchly.ui.theme.MunchlyColors

/**
 * Dialog for adding a new tag to a restaurant.
 * Handles validation and user feedback locally before confirming.
 */
@Composable
fun AddTagDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, (String?) -> Unit) -> Unit,
    existingTags: List<String>,
    modifier: Modifier = Modifier
) {
    var tagText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val handleConfirm = {
        onConfirm(tagText.trim()) { error ->
            if (error != null) {
                errorMessage = error
            } else {
                onDismiss()
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Tag",
                color = MunchlyColors.textPrimary
            )
        },
        text = {
            Column {
                Text(
                    text = "Examples: Italian, Vegan, Cafe, Breakfast, etc.",
                    color = MunchlyColors.textSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                ValidationTextField(
                    value = tagText,
                    onValueChange = {
                        tagText = it
                        errorMessage = null
                    },
                    placeholder = "Enter tag",
                    errorMessage = errorMessage,
                    imeAction = ImeAction.Done,
                    onImeAction = handleConfirm
                )
            }
        },
        confirmButton = {
            TextButton(onClick = handleConfirm) {
                Text(
                    text = "Add",
                    color = MunchlyColors.primary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    color = MunchlyColors.textSecondary
                )
            }
        },
        containerColor = MunchlyColors.surface,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    )
}