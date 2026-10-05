package com.social.vitadrop.presentation.splash

/* Dark Pallete
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kotlinbasics.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    // Futuristic 2030 Cyber-Medical Palette
    val bgDark = Color(0xFF090A0F)
    val cyberRed = Color(0xFFFF0033)
    val neonCrimson = Color(0xFFFF2A55)
    val textPrimary = Color(0xFFF0F2F8)
    val textMuted = Color(0xFF7E849B)

    // ANIMATION
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scaleAnim"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alphaAnim"
    )

    LaunchedEffect(Unit) {

        delay(2500)

        val user = auth.currentUser

        if (user != null) {

            db.collection("users")
                .document(user.uid)
                .get()
                .addOnSuccessListener { doc ->

                    val role = doc.getString("role") ?: "patient"

                    navController.navigate("dashboard/$role") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
                .addOnFailureListener {

                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }

        } else {

            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    // UI (2030 Futuristic Redesign)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgDark)
    ) {
        // Ambient Neon Top Radial Glows
        Box(
            modifier = Modifier
                .size(340.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-60).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(cyberRed.copy(alpha = 0.25f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )

        // Ambient Center Halo Glow
        Box(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.Center)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(neonCrimson.copy(alpha = 0.15f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )

        // TOP CYBER HUD & AMBIENT ELEMENTS
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Futuristic Status HUD Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(cyberRed.copy(alpha = 0.12f))
                    .border(0.8.dp, cyberRed.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(cyberRed)
                        .alpha(alpha)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "VITADROP v3.0 // BIO-LINK INITIALIZING",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = cyberRed,
                    letterSpacing = 1.2.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "❤",
                    fontSize = 20.sp,
                    color = cyberRed,
                    modifier = Modifier
                        .alpha(alpha * 0.4f)
                        .scale(scale)
                )

                Text(
                    text = "❤",
                    fontSize = 28.sp,
                    color = neonCrimson,
                    modifier = Modifier
                        .alpha(alpha * 0.5f)
                        .scale(scale * 0.9f)
                )
            }
        }

        // MAIN CENTER CONTENT
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // LOGO IMAGE WITH CYBER HALO
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(190.dp)
                    .scale(scale)
            ) {
                // Glow Halo Ring
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .clip(CircleShape)
                        .border(1.dp, cyberRed.copy(alpha = 0.3f), CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(cyberRed.copy(alpha = 0.2f), Color.Transparent)
                            )
                        )
                )

                Image(
                    painter = painterResource(id = R.drawable.blood_drop),
                    contentDescription = "Blood Drop",
                    modifier = Modifier
                        .size(150.dp)
                        .alpha(alpha),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // APP NAME
            Text(
                text = buildAnnotatedString {

                    withStyle(
                        style = SpanStyle(
                            color = textPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("Vita")
                    }

                    withStyle(
                        style = SpanStyle(
                            color = cyberRed,
                            fontWeight = FontWeight.ExtraBold
                        )
                    ) {
                        append("Drop")
                    }
                },
                fontSize = 36.sp,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "NEURAL BLOOD SYNC PLATFORM",
                fontSize = 10.sp,
                color = textMuted,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp
            )
        }

        // BOTTOM SECTION
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // HANDS IMAGE
            Image(
                painter = painterResource(id = R.drawable.hand_blood),
                contentDescription = "Helping Hands",
                modifier = Modifier
                    .size(180.dp)
                    .alpha(0.85f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Smart Blood Donation &",
                fontSize = 13.sp,
                color = textPrimary,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "Emergency Support Platform",
                fontSize = 12.sp,
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
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kotlinbasics.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    // Premium Fresh Palette
    val bgCanvas = Color(0xFFF8FAFC)
    val primaryRose = Color(0xFFE11D48)
    val deepRose = Color(0xFFBE123C)
    val softRoseBg = Color(0xFFFFF1F2)
    val freshEmerald = Color(0xFF10B981)
    val textDark = Color(0xFF0F172A)
    val textMuted = Color(0xFF64748B)

    // ANIMATION
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scaleAnim"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alphaAnim"
    )

    LaunchedEffect(Unit) {
        delay(2500)
        val user = auth.currentUser

        if (user != null) {
            db.collection("users")
                .document(user.uid)
                .get()
                .addOnSuccessListener { doc ->
                    val role = doc.getString("role") ?: "patient"
                    navController.navigate("dashboard/$role") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
                .addOnFailureListener {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
        } else {
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    // UI (Fresh Premium Theme)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF),
                        bgCanvas,
                        Color(0xFFFFF1F2)
                    )
                )
            )
    ) {
        // Subtle Soft Radial Glows
        Box(
            modifier = Modifier
                .size(340.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-80).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(softRoseBg, Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )

        // TOP STATUS BADGE
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(freshEmerald)
                        .alpha(alpha)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "VITADROP // BIO-LINK READY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = textMuted,
                    letterSpacing = 1.sp
                )
            }
        }

        // CENTER MAIN CONTENT
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(180.dp)
                    .scale(scale)
            ) {
                // Gentle Soft Halo
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .background(softRoseBg)
                )

                Image(
                    painter = painterResource(id = R.drawable.blood_drop),
                    contentDescription = "Blood Drop",
                    modifier = Modifier
                        .size(140.dp)
                        .alpha(alpha),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = textDark,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("Vita")
                    }
                    withStyle(
                        style = SpanStyle(
                            color = primaryRose,
                            fontWeight = FontWeight.ExtraBold
                        )
                    ) {
                        append("Drop")
                    }
                },
                fontSize = 38.sp,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "SMART DONATION & EMERGENCY NETWORK",
                fontSize = 11.sp,
                color = textMuted,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
        }

        // BOTTOM ILLUSTRATION & SUBTEXT
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.hand_blood),
                contentDescription = "Helping Hands",
                modifier = Modifier
                    .size(170.dp)
                    .alpha(0.9f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Connecting Donors & Hospitals Instantly",
                fontSize = 13.sp,
                color = textDark,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}