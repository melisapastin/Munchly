package com.example.munchly.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.munchly.ui.components.AuthFieldLabel
import com.example.munchly.ui.components.MenuPdfUpload
import com.example.munchly.ui.components.OpeningHoursSelector
import com.example.munchly.ui.components.PriceRangeSelector
import com.example.munchly.ui.components.RestaurantImagesUpload
import com.example.munchly.ui.components.TagsInput
import com.example.munchly.ui.components.ValidationTextField
import com.example.munchly.ui.theme.MunchlyColors
import com.example.munchly.ui.viewmodels.OwnerProfileState
import com.example.munchly.ui.viewmodels.OwnerProfileViewModel

/**
 * Edit form for restaurant profile.
 * Used for both creating new restaurants and editing existing ones.
 */
@Composable
fun OwnerProfileEditForm(
    state: OwnerProfileState,
    viewModel: OwnerProfileViewModel,
    onPdfUploadClick: () -> Unit,
    onImageUploadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MunchlyColors.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Basic Information Section
        item {
            SectionHeader(
                title = "Basic Information",
                subtitle = "Tell customers about your restaurant"
            )
        }

        item {
            AuthFieldLabel("Restaurant Name *")
            Spacer(modifier = Modifier.height(4.dp))
            ValidationTextField(
                value = state.name,
                onValueChange = viewModel::onNameChange,
                placeholder = "Enter restaurant name",
                errorMessage = state.nameError,
                imeAction = ImeAction.Next
            )
        }

        item {
            AuthFieldLabel("Description *")
            Spacer(modifier = Modifier.height(4.dp))
            ValidationTextField(
                value = state.description,
                onValueChange = viewModel::onDescriptionChange,
                placeholder = "Tell customers about your restaurant (min 20 characters)",
                errorMessage = state.descriptionError,
                imeAction = ImeAction.Next,
                singleLine = false
            )
        }

        item {
            AuthFieldLabel("Tags * (e.g., Italian, Vegan, Cafe)")
            Spacer(modifier = Modifier.height(8.dp))
            TagsInput(
                tags = state.tags,
                onTagAdd = viewModel::showTagDialog,
                onTagRemove = viewModel::removeTag,
                isError = state.tagsError != null
            )
            if (state.tagsError != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = state.tagsError,
                    color = MunchlyColors.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
        }

        item {
            AuthFieldLabel("Price Range *")
            Spacer(modifier = Modifier.height(8.dp))
            PriceRangeSelector(
                selectedRange = state.priceRange,
                onRangeSelected = viewModel::onPriceRangeChange
            )
        }

        // Contact Information Section
        item {
            Spacer(modifier = Modifier.height(8.dp))
            SectionHeader(
                title = "Contact Information",
                subtitle = "How customers can reach you"
            )
        }

        item {
            AuthFieldLabel("Address *")
            Spacer(modifier = Modifier.height(4.dp))
            ValidationTextField(
                value = state.address,
                onValueChange = viewModel::onAddressChange,
                placeholder = "Full address",
                errorMessage = state.addressError,
                imeAction = ImeAction.Next
            )
        }

        item {
            AuthFieldLabel("Phone * (Romanian number)")
            Spacer(modifier = Modifier.height(4.dp))
            ValidationTextField(
                value = state.phone,
                onValueChange = viewModel::onPhoneChange,
                placeholder = "+40 7XX XXX XXX or 07XX XXX XXX",
                errorMessage = state.phoneError,
                imeAction = ImeAction.Next
            )
        }

        // Business Hours Section
        item {
            Spacer(modifier = Modifier.height(8.dp))
            SectionHeader(
                title = "Business Hours",
                subtitle = "Set your operating schedule"
            )
        }

        item {
            OpeningHoursSelector(
                schedule = state.openingHours,
                onScheduleChange = viewModel::onScheduleChange
            )
            if (state.openingHoursError != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = state.openingHoursError,
                    color = MunchlyColors.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
        }

        // Media & Menu Section
        item {
            Spacer(modifier = Modifier.height(8.dp))
            SectionHeader(
                title = "Media & Menu",
                subtitle = "Showcase your restaurant (optional)"
            )
        }

        item {
            val hasMenuPdf = state.menuPdfUri != null || state.menuPdfUrl.isNotEmpty()
            MenuPdfUpload(
                menuPdfUrl = if (hasMenuPdf) "uploaded" else null,
                onUploadClick = onPdfUploadClick,
                onRemoveClick = viewModel::removeMenuPdf
            )
        }

        item {
            RestaurantImagesUpload(
                images = state.images,
                onAddImage = onImageUploadClick,
                onRemoveImage = viewModel::removeImage
            )
        }

        // Error Message
        if (state.error != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MunchlyColors.errorBackground
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = state.error,
                        color = MunchlyColors.error,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        // Action Buttons
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (state.hasRestaurant) {
                    OutlinedButton(
                        onClick = viewModel::cancelEditing,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        enabled = !state.isSaving,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = MunchlyColors.textPrimary
                        )
                    ) {
                        Text(
                            text = "Cancel",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Button(
                    onClick = viewModel::saveRestaurant,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    enabled = !state.isSaving,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MunchlyColors.primary,
                        contentColor = Color.White,
                        disabledContainerColor = MunchlyColors.primary.copy(alpha = 0.6f)
                    )
                ) {
                    Text(
                        text = if (state.hasRestaurant) "Save Changes" else "Create Restaurant",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            if (state.isSaving) {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MunchlyColors.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/**
 * Section header card for organizing form sections.
 */
@Composable
private fun SectionHeader(
    title: String,
    subtitle: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MunchlyColors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MunchlyColors.textPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = MunchlyColors.textSecondary
            )
        }
    }
}