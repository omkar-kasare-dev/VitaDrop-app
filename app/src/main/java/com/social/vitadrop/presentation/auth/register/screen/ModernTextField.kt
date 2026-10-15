package com.social.vitadrop.presentation.auth.register.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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
    var isFocused by remember { mutableStateOf(false) }

    // Minimal White Theme Palette
    val bgUnfocused = Color(0xFFF8F9FA)
    val bgFocused = Color(0xFFFFFFFF)
    val borderUnfocused = Color(0xFFE5E7EB)
    val borderFocused = Color(0xFFE53935)
    val textPrimary = Color(0xFF111827)
    val textMuted = Color(0xFF9CA3AF)
    val iconMuted = Color(0xFF6B7280)

    val animatedBgColor by animateColorAsState(
        targetValue = if (isFocused) bgFocused else bgUnfocused,
        animationSpec = tween(durationMillis = 150),
        label = "bgColorAnimation"
    )
    val animatedBorderColor by animateColorAsState(
        targetValue = if (isFocused) borderFocused else borderUnfocused,
        animationSpec = tween(durationMillis = 150),
        label = "borderColorAnimation"
    )

    val visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { isFocused = it.isFocused },
        textStyle = TextStyle(
            color = textPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        singleLine = true,
        cursorBrush = SolidColor(borderFocused),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .background(color = animatedBgColor, shape = RoundedCornerShape(12.dp))
                    .border(
                        width = if (isFocused) 1.5.dp else 1.dp,
                        color = animatedBorderColor,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    CompositionLocalProvider(
                        LocalContentColor provides if (isFocused) borderFocused else iconMuted
                    ) {
                        icon()
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                }

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = label,
                            fontSize = 14.sp,
                            color = textMuted,
                            fontWeight = FontWeight.Normal
                        )
                    }
                    innerTextField()
                }
            }
        }
    )
}