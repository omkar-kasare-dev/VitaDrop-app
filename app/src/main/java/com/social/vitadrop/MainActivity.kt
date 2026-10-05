package com.social.vitadrop

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.messaging.FirebaseMessaging

import com.social.vitadrop.presentation.navigation.NavGraph
import com.social.vitadrop.ui.theme.VitaDropTheme
import com.social.vitadrop.utils.NotificationHelper


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
       // subscribeToDonorsTopic()
        NotificationHelper(this).createNotificationChannel()
        requestNotificationPermission()
        setContent { VitaDropTheme { NavGraph() } }
    }


    // =============================================================
    // TEST 1: FIRESTORE SERVER-ONLY READ
    // =============================================================

    private fun testFirestoreServerConnection() {

        val db = FirebaseFirestore.getInstance()

        Log.d(
            "VITA_FIRESTORE_SERVER",
            "Starting server-only Firestore read..."
        )

        db.collection("test")
            .document("connection_test")
            .get(Source.SERVER)
            .addOnSuccessListener { document ->

                Log.d(
                    "VITA_FIRESTORE_SERVER",
                    "SUCCESS: Server connection established"
                )

                Log.d(
                    "VITA_FIRESTORE_SERVER",
                    "Document exists = ${document.exists()}"
                )

                Log.d(
                    "VITA_FIRESTORE_SERVER",
                    "Data = ${document.data}"
                )
            }
            .addOnFailureListener { exception ->

                Log.e(
                    "VITA_FIRESTORE_SERVER",
                    "FAILED: Server-only Firestore read failed",
                    exception
                )
            }
    }


    // =============================================================
    // TEST 2: NORMAL FIRESTORE WRITE
    // =============================================================

    private fun testFirestoreWrite() {

        val db = FirebaseFirestore.getInstance()

        Log.d(
            "VITA_FIRESTORE_TEST",
            "Firestore instance created"
        )

        db.enableNetwork()
            .addOnCompleteListener { networkTask ->

                Log.d(
                    "VITA_FIRESTORE_TEST",
                    "enableNetwork completed = ${networkTask.isSuccessful}"
                )

                if (!networkTask.isSuccessful) {

                    Log.e(
                        "VITA_FIRESTORE_TEST",
                        "enableNetwork failed",
                        networkTask.exception
                    )

                    return@addOnCompleteListener
                }

                Log.d(
                    "VITA_FIRESTORE_TEST",
                    "Starting test write..."
                )

                val testData = mapOf(
                    "message" to "Firestore test",
                    "timestamp" to System.currentTimeMillis()
                )

                db.collection("test")
                    .document("connection_test")
                    .set(testData)
                    .addOnSuccessListener {

                        Log.d(
                            "VITA_FIRESTORE_TEST",
                            "SUCCESS: Firestore test write completed"
                        )
                    }
                    .addOnFailureListener { exception ->

                        Log.e(
                            "VITA_FIRESTORE_TEST",
                            "FAILED: Firestore test write failed",
                            exception
                        )
                    }
            }
    }


    // =============================================================
    // FCM TOPIC SUBSCRIPTION
    // =============================================================

    private fun fetchFcmToken() {

        FirebaseMessaging.getInstance()
            .subscribeToTopic("donors")
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    Log.d(
                        "TOPIC",
                        "Subscribed to donors"
                    )

                } else {

                    Log.e(
                        "TOPIC",
                        "Subscription failed",
                        task.exception
                    )
                }
            }
    }


    // =============================================================
    // NOTIFICATION PERMISSION
    // =============================================================

    private fun requestNotificationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.POST_NOTIFICATIONS
                    ),
                    101
                )
            }
        }
    }
}