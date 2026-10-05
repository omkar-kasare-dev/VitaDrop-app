package com.social.vitadrop.presentation.components
/*
Dark Pallete
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.kotlinbasics.R

enum class VitaDropLoaderStyle {
    FullScreen,
    Inline,
    Dialog
}

/**
 * Common Cyber-Medical Progress Loader for VitaDrop App
 *
 * @param modifier Modifier for custom layout adjustments
 * @param style Style of the loader: FullScreen, Inline, or Dialog
 * @param message Main instruction or loading status text
 * @param subMessage Technical telemetry or sub-text
 */
@Composable
fun VitaDropLoader(
    modifier: Modifier = Modifier,
    style: VitaDropLoaderStyle = VitaDropLoaderStyle.FullScreen,
    message: String = "INITIALIZING VITA-LINK",
    subMessage: String = "Synchronizing bio-metrics & network..."
) {
    when (style) {
        VitaDropLoaderStyle.FullScreen -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color(0xFF090A0F)),
                contentAlignment = Alignment.Center
            ) {
                LoaderContent(message = message, subMessage = subMessage, size = 120.dp)
            }
        }
        VitaDropLoaderStyle.Inline -> {
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF12141D)),
                border = androidx.compose.foundation.BorderStroke(0.8.dp, Color(0xFFFF0033).copy(alpha = 0.3f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LoaderContent(message = message, subMessage = subMessage, size = 80.dp)
                }
            }
        }
        VitaDropLoaderStyle.Dialog -> {
            Dialog(
                onDismissRequest = { },
                properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF0D0F17))
                        .border(1.dp, Color(0xFFFF0033).copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                        .padding(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LoaderContent(message = message, subMessage = subMessage, size = 90.dp)
                }
            }
        }
    }
}

@Composable
private fun LoaderContent(
    message: String,
    subMessage: String,
    size: Dp
) {
    val cyberRed = Color(0xFFFF0033)
    val neonCrimson = Color(0xFFFF2A55)
    val textPrimary = Color(0xFFF0F2F8)
    val textMuted = Color(0xFF7E849B)

    // Continuous Infinite Animations
    val infiniteTransition = rememberInfiniteTransition(label = "loader_anim")

    // Continuous Rotation for Outer Ring
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Gentle Pulse Scale for Blood Drop
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Glowing Alpha Animation
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha_glow"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // CYBER LOADER GRAPHIC
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(size + 40.dp)
        ) {
            // Background Ambient Radial Glow
            Box(
                modifier = Modifier
                    .size(size + 20.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(cyberRed.copy(alpha = 0.25f), Color.Transparent)
                        ),
                        shape = CircleShape
                    )
            )

            // Rotating Cyber Orbit Ring
            Box(
                modifier = Modifier
                    .size(size)
                    .rotate(rotation)
                    .border(
                        width = 2.dp,
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                cyberRed,
                                neonCrimson.copy(alpha = 0.2f),
                                Color.Transparent,
                                cyberRed
                            )
                        ),
                        shape = CircleShape
                    )
            )

            // Inner Pulsing Halo
            Box(
                modifier = Modifier
                    .size(size * 0.75f)
                    .scale(scale)
                    .clip(CircleShape)
                    .border(1.dp, cyberRed.copy(alpha = 0.5f), CircleShape)
                    .background(cyberRed.copy(alpha = 0.1f))
            )

            // Central Blood Drop Image
            Image(
                painter = painterResource(id = R.drawable.blood_drop),
                contentDescription = "Loading Drop",
                modifier = Modifier
                    .size(size * 0.5f)
                    .scale(scale)
                    .alpha(alpha),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // HUD TELEMETRY BADGE
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(cyberRed.copy(alpha = 0.1f))
                .border(0.6.dp, cyberRed.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(cyberRed)
                    .alpha(alpha)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = message.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = cyberRed,
                letterSpacing = 1.1.sp
            )
        }

        if (subMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subMessage,
                fontSize = 11.sp,
                color = textMuted,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            )
        }
    }
}

 */


import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.kotlinbasics.R

enum class VitaDropLoaderStyle {
    FullScreen,
    Inline,
    Dialog
}

@Composable
fun VitaDropLoader(
    modifier: Modifier = Modifier,
    style: VitaDropLoaderStyle = VitaDropLoaderStyle.FullScreen,
    message: String = "SYNCHRONIZING VITA-LINK",
    subMessage: String = "Connecting medical network..."
) {
    when (style) {
        VitaDropLoaderStyle.FullScreen -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8FAFC)),
                contentAlignment = Alignment.Center
            ) {
                LoaderContent(message = message, subMessage = subMessage, size = 100.dp)
            }
        }
        VitaDropLoaderStyle.Inline -> {
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LoaderContent(message = message, subMessage = subMessage, size = 70.dp)
                }
            }
        }
        VitaDropLoaderStyle.Dialog -> {
            Dialog(
                onDismissRequest = { },
                properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(24.dp))
                        .padding(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LoaderContent(message = message, subMessage = subMessage, size = 80.dp)
                }
            }
        }
    }
}

@Composable
private fun LoaderContent(
    message: String,
    subMessage: String,
    size: Dp
) {
    val primaryRose = Color(0xFFE11D48)
    val softRoseBg = Color(0xFFFFF1F2)
    val freshEmerald = Color(0xFF10B981)
    val textDark = Color(0xFF0F172A)
    val textMuted = Color(0xFF64748B)

    val infiniteTransition = rememberInfiniteTransition(label = "loader_anim")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.90f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(size + 30.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(size)
                    .rotate(rotation)
                    .border(
                        width = 2.dp,
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                primaryRose,
                                primaryRose.copy(alpha = 0.15f),
                                Color.Transparent,
                                primaryRose
                            )
                        ),
                        shape = CircleShape
                    )
            )

            Box(
                modifier = Modifier
                    .size(size * 0.72f)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(softRoseBg)
            )

            Image(
                painter = painterResource(id = R.drawable.blood_drop),
                contentDescription = "Loading Drop",
                modifier = Modifier
                    .size(size * 0.45f)
                    .scale(scale),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(softRoseBg)
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(freshEmerald)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = message.uppercase(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = primaryRose,
                letterSpacing = 1.sp
            )
        }

        if (subMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subMessage,
                fontSize = 11.sp,
                color = textMuted,
                fontWeight = FontWeight.Medium
            )
        }
    }
}