package com.social.vitadrop.presentation.splash
/* Main
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
// IMPORTANT: use the R class of your app "namespace" (see app/build.gradle).
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
/*
        // TOP STATUS BADGE
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

        }

 */

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
/*
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

 */
    }


}