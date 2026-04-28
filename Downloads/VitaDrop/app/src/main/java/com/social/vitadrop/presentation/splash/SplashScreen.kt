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

            // 🔥 FETCH ROLE FROM FIRESTORE
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

    // 🎨 UI (UNCHANGED)
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