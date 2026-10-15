package com.social.vitadrop.presentation.dashboard.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp

// =====================================================================
// FUTURISTIC BLOOD NETWORK AI FLOATING ACTION BUTTON
// =====================================================================
/*
@Composable
fun FuturisticBloodNetworkAIFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "blood_network_anim")

    val pulseScale1 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse1"
    )
    val pulseAlpha1 by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha1"
    )

    val pulseScale2 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, delayMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse2"
    )
    val pulseAlpha2 by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, delayMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha2"
    )

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_rotation"
    )

    val primaryBloodRed = Color(0xFFE11D48)
    val neonCyberRed = Color(0xFFFF2A55)
    val deepCrimson = Color(0xFF880E4F)

    val clickScale = if (isPressed) 0.92f else 1f

    Box(
        modifier = modifier
            .size(72.dp)
            .scale(clickScale),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerOffset = Offset(size.width / 2f, size.height / 2f)
            val baseRadius = (size.minDimension / 2f) * 0.75f

            drawCircle(
                color = neonCyberRed.copy(alpha = pulseAlpha1),
                radius = baseRadius * pulseScale1,
                center = centerOffset
            )

            drawCircle(
                color = primaryBloodRed.copy(alpha = pulseAlpha2),
                radius = baseRadius * pulseScale2,
                center = centerOffset
            )

            rotate(degrees = rotationAngle, pivot = centerOffset) {
                drawCircle(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            neonCyberRed.copy(alpha = 0.9f),
                            primaryBloodRed.copy(alpha = 0.1f),
                            Color.Transparent,
                            neonCyberRed.copy(alpha = 0.8f)
                        )
                    ),
                    radius = baseRadius + 4.dp.toPx(),
                    center = centerOffset,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }

        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(neonCyberRed, primaryBloodRed, deepCrimson)
                    )
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(Color.White.copy(alpha = 0.8f), neonCyberRed.copy(alpha = 0.3f))
                    ),
                    shape = CircleShape
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(26.dp)) {
                val w = size.width
                val h = size.height

                val bloodDropPath = Path().apply {
                    moveTo(w * 0.5f, 0f)
                    cubicTo(
                        w * 0.1f, h * 0.55f,
                        0f, h * 0.72f,
                        w * 0.5f, h
                    )
                    cubicTo(
                        w, h * 0.72f,
                        w * 0.9f, h * 0.55f,
                        w * 0.5f, 0f
                    )
                    close()
                }

                drawPath(
                    path = bloodDropPath,
                    color = Color.White
                )

                drawCircle(
                    color = neonCyberRed,
                    radius = w * 0.15f,
                    center = Offset(w * 0.5f, h * 0.65f)
                )
                drawCircle(
                    color = Color.White,
                    radius = w * 0.07f,
                    center = Offset(w * 0.5f, h * 0.65f)
                )
            }
        }
    }
}

 */
/*
@Composable
fun FuturisticBloodNetworkAIFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "bio_neural_anim")

    // Pulsing energy ring scale & alpha
    val pulseDuration = 2200
    val ringScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(pulseDuration, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring_scale"
    )
    val ringAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(pulseDuration, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring_alpha"
    )

    // Continuous scanning wave rotation
    val scanAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scan_angle"
    )

    val crimsonCore = Color(0xFFDC2626)
    val cyberPink = Color(0xFFF43F5E)
    val deepNavyGlow = Color(0xFF1E1B4B)
    val techCyan = Color(0xFF38BDF8)

    val clickScale = if (isPressed) 0.92f else 1f

    Box(
        modifier = modifier
            .size(76.dp)
            .scale(clickScale),
        contentAlignment = Alignment.Center
    ) {
        // Background Neural/DNA Energy Waves Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerOffset = Offset(size.width / 2f, size.height / 2f)
            val radius = (size.minDimension / 2f) * 0.78f

            // Expanding Bio-Pulse Energy Ring
            drawCircle(
                color = cyberPink.copy(alpha = ringAlpha),
                radius = radius * ringScale,
                center = centerOffset,
                style = Stroke(width = 1.5.dp.toPx())
            )

            // Rotating Cybernetic Tech Nodes / Slices
            rotate(degrees = scanAngle, pivot = centerOffset) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            techCyan.copy(alpha = 0.7f),
                            Color.Transparent,
                            cyberPink.copy(alpha = 0.8f),
                            Color.Transparent
                        )
                    ),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = Offset(centerOffset.x - radius - 2.dp.toPx(), centerOffset.y - radius - 2.dp.toPx()),
                    size = androidx.compose.ui.geometry.Size((radius + 2.dp.toPx()) * 2f, (radius + 2.dp.toPx()) * 2f),
                    style = Stroke(width = 2.5.dp.toPx())
                )
            }
        }

        // Inner Tech Hexagon Button Core
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(cyberPink, crimsonCore, deepNavyGlow)
                    )
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(techCyan.copy(alpha = 0.8f), Color.White.copy(alpha = 0.4f))
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            // Futuristic AI Medical Blood Cross / Node Icon Canvas
            Canvas(modifier = Modifier.size(28.dp)) {
                val w = size.width
                val h = size.height
                val cx = w / 2f
                val cy = h / 2f

                // Central Cross (Medical Node)
                val barWidth = w * 0.22f
                val barLength = w * 0.7f

                // Vertical bar
                drawRect(
                    color = Color.White,
                    topLeft = Offset(cx - barWidth / 2f, cy - barLength / 2f),
                    size = androidx.compose.ui.geometry.Size(barWidth, barLength)
                )
                // Horizontal bar
                drawRect(
                    color = Color.White,
                    topLeft = Offset(cx - barLength / 2f, cy - barWidth / 2f),
                    size = androidx.compose.ui.geometry.Size(barLength, barWidth)
                )

                // Glowing AI Neural Core Dot in Center
                drawCircle(
                    color = techCyan,
                    radius = w * 0.18f,
                    center = Offset(cx, cy)
                )
                drawCircle(
                    color = Color.White,
                    radius = w * 0.08f,
                    center = Offset(cx, cy)
                )
            }
        }
    }
}

 */
/*
@Composable
fun FuturisticBloodNetworkAIFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "blood_cell_ai_anim")

    // Organic Cell Breathing Animation
    val cellScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cell_scale"
    )

    // Orbiting Cyber Signal Rings
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring_rotation"
    )

    val bloodRed = Color(0xFFE11D48)
    val glowingCrimson = Color(0xFFFB7185)
    val deepPlasma = Color(0xFF881337)
    val aiCyanGlow = Color(0xFF38BDF8)

    val clickScale = if (isPressed) 0.92f else 1f

    Box(
        modifier = modifier
            .size(72.dp)
            .scale(clickScale),
        contentAlignment = Alignment.Center
    ) {
        // Futuristic Orbit Signals
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerOffset = Offset(size.width / 2f, size.height / 2f)
            val outerRadius = (size.minDimension / 2f) * 0.88f

            // Outer Digital Signal Ring
            rotate(degrees = rotationAngle, pivot = centerOffset) {
                drawCircle(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            glowingCrimson.copy(alpha = 0.8f),
                            Color.Transparent,
                            aiCyanGlow.copy(alpha = 0.6f),
                            Color.Transparent
                        )
                    ),
                    radius = outerRadius,
                    center = centerOffset,
                    style = Stroke(width = 1.8.dp.toPx())
                )

                // Network Orbit Node Particle
                drawCircle(
                    color = aiCyanGlow,
                    radius = 2.5.dp.toPx(),
                    center = Offset(centerOffset.x + outerRadius, centerOffset.y)
                )
            }
        }

        // Main Animated Blood Cell Fab Body
        Box(
            modifier = Modifier
                .scale(cellScale)
                .size(54.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(glowingCrimson, bloodRed, deepPlasma),
                        center = Offset(0.3f, 0.3f)
                    )
                )
                .border(
                    width = 1.2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(Color.White.copy(alpha = 0.7f), glowingCrimson.copy(alpha = 0.2f))
                    ),
                    shape = CircleShape
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            // Blood Drop & Neural Crosshair Icon
            Canvas(modifier = Modifier.size(24.dp)) {
                val w = size.width
                val h = size.height

                // Blood Drop Silhouette
                val bloodDropPath = Path().apply {
                    moveTo(w * 0.5f, 0f)
                    cubicTo(
                        w * 0.12f, h * 0.52f,
                        0f, h * 0.7f,
                        w * 0.5f, h
                    )
                    cubicTo(
                        w, h * 0.7f,
                        w * 0.88f, h * 0.52f,
                        w * 0.5f, 0f
                    )
                    close()
                }

                drawPath(
                    path = bloodDropPath,
                    color = Color.White
                )

                // AI Neural Core Ring Inside Drop
                drawCircle(
                    color = bloodRed,
                    radius = w * 0.18f,
                    center = Offset(w * 0.5f, h * 0.64f)
                )
                drawCircle(
                    color = aiCyanGlow,
                    radius = w * 0.08f,
                    center = Offset(w * 0.5f, h * 0.64f)
                )
            }
        }
    }
}

 */

@Composable
fun FuturisticBloodNetworkAIFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "ecg_pulse_anim")

    // ECG Heartbeat Pulsing Rings
    val waveScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_scale"
    )

    val waveAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_alpha"
    )

    val roseRed = Color(0xFF3D75C1)
    val lightRose = Color(0xFF849DDD)
    val darkRed = Color(0xFF05124C)

    val clickScale = if (isPressed) 0.92f else 1f

    Box(
        modifier = modifier
            .size(72.dp)
            .scale(clickScale),
        contentAlignment = Alignment.Center
    ) {
        // Expanding ECG Pulse Rings Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerOffset = Offset(size.width / 2f, size.height / 2f)
            val baseRadius = (size.minDimension / 2f) * 0.72f

            // Radial Pulse Ring
            drawCircle(
                color = lightRose.copy(alpha = waveAlpha),
                radius = baseRadius * waveScale,
                center = centerOffset,
                style = Stroke(width = 2.dp.toPx())
            )
        }

        // Inner Core Medical Shield Button
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(lightRose, roseRed, darkRed)
                    )
                )
                .border(
                    width = 1.5.dp,
                    color = Color.White.copy(alpha = 0.8f),
                    shape = CircleShape
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            // ECG Heartbeat & AI Spark Icon
            Canvas(modifier = Modifier.size(26.dp)) {
                val w = size.width
                val h = size.height

                // ECG Signal Line Path
                val ecgPath = Path().apply {
                    moveTo(0f, h * 0.5f)
                    lineTo(w * 0.25f, h * 0.5f)
                    lineTo(w * 0.35f, h * 0.25f)
                    lineTo(w * 0.5f, h * 0.8f)
                    lineTo(w * 0.65f, h * 0.15f)
                    lineTo(w * 0.75f, h * 0.5f)
                    lineTo(w, h * 0.5f)
                }

                drawPath(
                    path = ecgPath,
                    color = Color.White,
                    style = Stroke(width = 2.2.dp.toPx())
                )

                // AI Node Core at Peak
                drawCircle(
                    color = Color.White,
                    radius = 3.dp.toPx(),
                    center = Offset(w * 0.65f, h * 0.15f)
                )
            }
        }
    }
}