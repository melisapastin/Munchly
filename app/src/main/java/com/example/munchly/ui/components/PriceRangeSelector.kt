package com.example.munchly.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.munchly.domain.models.PriceRangeDomain
import com.example.munchly.ui.theme.MunchlyColors

/**
 * Component for selecting restaurant price range.
 * Displays three options: Budget, Medium, Expensive.
 */
@Composable
fun PriceRangeSelector(
    selectedRange: PriceRangeDomain,
    onRangeSelected: (PriceRangeDomain) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PriceRangeDomain.entries.forEach { range ->
            PriceRangeOption(
                priceRange = range,
                isSelected = selectedRange == range,
                onClick = { onRangeSelected(range) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Individual price range option card.
 */
@Composable
private fun PriceRangeOption(
    priceRange: PriceRangeDomain,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MunchlyColors.primaryLight
            } else {
                MunchlyColors.surface
            }
        ),
        border = BorderStroke(
            width = 2.dp,
            color = if (isSelected) {
                MunchlyColors.primary
            } else {
                MunchlyColors.borderDefault
            }
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = priceRange.symbol,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) {
                    MunchlyColors.primary
                } else {
                    MunchlyColors.textPrimary
                }
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = priceRange.getDescription(),
                fontSize = 12.sp,
                color = MunchlyColors.textSecondary,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

/**
 * Extension function to get user-friendly description for price range.
 * This is UI-level presentation logic, not business logic.
 */
private fun PriceRangeDomain.getDescription(): String {
    return when (this) {
        PriceRangeDomain.BUDGET -> "Budget-friendly"
        PriceRangeDomain.MEDIUM -> "Moderate"
        PriceRangeDomain.EXPENSIVE -> "Fine dining"
    }
}