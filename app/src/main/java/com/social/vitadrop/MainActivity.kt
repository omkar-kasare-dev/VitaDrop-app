package com.social.vitadrop

import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import com.social.vitadrop.presentation.navigation.NavGraph
import com.social.vitadrop.ui.theme.VitaDropTheme
import com.social.vitadrop.utils.NotificationHelper

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseFirestore.getInstance()
            .useEmulator("172.20.175.225", 8080)

        FirebaseAuth.getInstance()
            .useEmulator("172.20.175.225", 9099)


        // =========================
        // FCM TOKEN (FIXED - ONLY ONCE)
        // =========================
        fetchFcmToken()

        // =========================
        // Notification Channel Setup
        // =========================
        NotificationHelper(this)
            .createNotificationChannel()

        requestNotificationPermission()

        enableEdgeToEdge()

        setContent {
            VitaDropTheme {
                NavGraph()
            }
        }
    }

    // =========================
    // CLEAN FCM TOKEN METHOD
    // =========================
    private fun fetchFcmToken() {
        FirebaseMessaging.getInstance().subscribeToTopic("donors")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("TOPIC", "Subscribed to donors")
                } else {
                    Log.e("TOPIC", "Subscription failed")
                }
            }
       /*
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->

                if (!task.isSuccessful) {
                    Log.e("VITA_FCM_TOKEN", "Token fetch failed", task.exception)
                    return@addOnCompleteListener
                }

                val token = task.result

                // IMPORTANT: single clean log for Postman copy
                Log.d("VITA_FCM_TOKEN", "TOKEN => $token")
            }

        */
    }

    // =========================
    // NOTIFICATION PERMISSION
    // =========================
    private fun requestNotificationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }
    }
}