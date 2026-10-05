package com.social.vitadrop.presentation.auth.register.screen


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: @Composable (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    val bgSurface = Color(0xFF161925)
    val cyberRed = Color(0xFFFF0033)
    val glassBorder = Color(0xFF2A2E3D)
    val textPrimary = Color(0xFFF0F2F8)
    val textMuted = Color(0xFF7E849B)

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        placeholder = { Text(label, fontSize = 12.sp, color = textMuted) },
        leadingIcon = icon,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = bgSurface,
            unfocusedContainerColor = bgSurface,
            focusedBorderColor = cyberRed,
            unfocusedBorderColor = glassBorder,
            focusedTextColor = textPrimary,
            unfocusedTextColor = textPrimary
        )
    )
}