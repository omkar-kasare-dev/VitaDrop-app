/*
package com.social.vitadrop.presentation.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(Unit) {

        delay(2000)

        val user = auth.currentUser

        if (user != null) {

            //FETCH ROLE FROM FIRESTORE
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

    // UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Icon(
                imageVector = Icons.Default.Bloodtype,
                contentDescription = "VitaDrop Logo",
                tint = Color.Red,
                modifier = Modifier.size(90.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "VitaDrop",
                fontSize = 36.sp,
                color = Color.Red
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Donate Blood • Save Lives",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    }
}

 */
package com.social.vitadrop.presentation.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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

    // ANIMATION
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scaleAnim"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
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

    // UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White,
                        Color(0xFFFFF5F5),
                        Color(0xFFE00000)
                    )
                )
            )
    ) {

        // TOP HEARTS
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 80.dp),
            contentAlignment = Alignment.TopCenter
        ) {

            Text(
                text = "❤",
                fontSize = 28.sp,
                color = Color(0xFFFFCACA),
                modifier = Modifier
                    .offset(x = (-120).dp, y = 10.dp)
                    .alpha(0.5f)
            )

            Text(
                text = "❤",
                fontSize = 40.sp,
                color = Color(0xFFFFD6D6),
                modifier = Modifier
                    .offset(x = 120.dp, y = 40.dp)
                    .alpha(0.4f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // LOGO IMAGE
            Image(
                painter = painterResource(id = R.drawable.blood_drop),
                contentDescription = "Blood Drop",
                modifier = Modifier
                    .size(170.dp)
                    .scale(scale)
                    .alpha(alpha),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(28.dp))

            // APP NAME
            Text(
                text = buildAnnotatedString {

                    withStyle(
                        style = SpanStyle(
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("Vita")
                    }

                    withStyle(
                        style = SpanStyle(
                            color = Color(0xFFE00000),
                            fontWeight = FontWeight.ExtraBold
                        )
                    ) {
                        append("Drop")
                    }
                },
                fontSize = 42.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            // TAGLINE
            Text(
                text = "Every drop can save\na precious life.",
                fontSize = 20.sp,
                color = Color.DarkGray,
                lineHeight = 30.sp
            )
        }

        // BOTTOM SECTION
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // HANDS IMAGE
            Image(
                painter = painterResource(id = R.drawable.hand_blood),
                contentDescription = "Helping Hands",
                modifier = Modifier.size(220.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Smart Blood Donation &",
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Emergency Support Platform",
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}