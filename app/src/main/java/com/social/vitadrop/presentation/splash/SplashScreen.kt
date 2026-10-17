package com.social.vitadrop.presentation.splash

// IMPORTANT: use the R class of your app "namespace" (see app/build.gradle).
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.social.vitadrop.domain.model.UserRole
import com.social.vitadrop.utils.SessionManager
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {

    val context = LocalContext.current

    // Premium Fresh Palette
    val bgCanvas = Color(0xFFF8FAFC)
    val primaryRose = Color(0xFFE11D48)
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

    // WHERE TO GO AFTER THE SPLASH
    //
    // BEFORE: it read a document in a "users" collection. The app never writes that collection
    // (accounts live in "donors" and "hospitals"), so the role was always missing and the
    // fallback "patient" was used: every logged-in user, hospitals included, was sent to
    // "dashboard/patient", which showed the donor dashboard. It also needed the network,
    // so an offline start sent a logged-in user back to the login screen.
    //
    // NOW: the role saved at login is used. It was verified against donors/ or hospitals/
    // before it was saved. No network call is needed.
    LaunchedEffect(Unit) {
        delay(2500)

        val auth = FirebaseAuth.getInstance()
        val sessionManager = SessionManager(context.applicationContext)

        val role = UserRole.fromKey(sessionManager.getUserRole())
        val isLoggedIn = auth.currentUser != null && role != null

        if (!isLoggedIn) {
            // Keep both sides consistent: a Firebase user without a saved role, or a saved
            // role without a Firebase user, would otherwise cause odd screens later.
            auth.signOut()
            sessionManager.clearSession()
        }

        val destination = if (isLoggedIn && role != null) "dashboard/${role.key}" else "login"

        navController.navigate(destination) {
            popUpTo("splash") { inclusive = true }
        }
    }

    // UI (Fresh Premium Theme) - unchanged
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

    }


}