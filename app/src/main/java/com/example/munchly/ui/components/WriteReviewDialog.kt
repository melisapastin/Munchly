package com.example.munchly.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.munchly.ui.theme.MunchlyColors

/**
 * Dialog for writing a restaurant review.
 * Allows user to select rating and/or write comment (at least one required).
 */
@Composable
fun WriteReviewDialog(
    restaurantName: String,
    onDismiss: () -> Unit,
    onSubmit: (rating: Double, comment: String, onError: (String) -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    var rating by remember { mutableDoubleStateOf(0.0) }
    var comment by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val handleSubmit = {
        if (rating == 0.0 && comment.trim().isEmpty()) {
            errorMessage = "Please provide either a rating or a review comment"
        } else {
            onSubmit(rating, comment) { error ->
                errorMessage = error
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Review $restaurantName",
                fontWeight = FontWeight.Bold,
                color = MunchlyColors.textPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Rate your experience (optional)",
                    fontSize = 14.sp,
                    color = MunchlyColors.textSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                RatingBar(
                    rating = rating,
                    onRatingChange = { newRating ->
                        rating = newRating
                        errorMessage = null
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Write a review (optional)",
                    fontSize = 14.sp,
                    color = MunchlyColors.textSecondary,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                ValidationTextField(
                    value = comment,
                    onValueChange = {
                        comment = it
                        errorMessage = null
                    },
                    placeholder = "Share your experience (optional)...",
                    errorMessage = errorMessage,
                    imeAction = ImeAction.Done,
                    onImeAction = handleSubmit,
                    singleLine = false,
                    maxLines = 5
                )
            }
        },
        confirmButton = {
            TextButton(onClick = handleSubmit) {
                Text(
                    text = "Submit",
                    color = MunchlyColors.primary,
                    fontWeight = FontWeight.SemiBold
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