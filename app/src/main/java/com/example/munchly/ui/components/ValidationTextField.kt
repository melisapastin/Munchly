package com.example.munchly.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.munchly.ui.theme.MunchlyColors

/**
 * Reusable text field component with validation support.
 * Supports both regular text and password fields.
 */
@Composable
fun ValidationTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    isError: Boolean = false,
    isPassword: Boolean = false,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {},
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else 5
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = MunchlyColors.textPlaceholder
                )
            },
            modifier = Modifier.fillMaxWidth(),
            isError = isError || errorMessage != null,
            singleLine = singleLine,
            maxLines = maxLines,
            visualTransformation = if (isPassword) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MunchlyColors.primary,
                unfocusedBorderColor = MunchlyColors.borderDefault,
                errorBorderColor = MunchlyColors.error,
                focusedTextColor = MunchlyColors.textPrimary,
                unfocusedTextColor = MunchlyColors.textPrimary,

                focusedContainerColor = MunchlyColors.surface,
                unfocusedContainerColor = MunchlyColors.surface,
                disabledContainerColor = MunchlyColors.surface,
                errorContainerColor = MunchlyColors.surface
            ),
            keyboardOptions = KeyboardOptions(imeAction = imeAction),
            keyboardActions = KeyboardActions(
                onAny = { onImeAction() }
            )
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                color = MunchlyColors.error,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
