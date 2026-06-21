package com.social.vitadrop.presentation.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.kotlinbasics.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

import com.social.vitadrop.utils.NotificationHelper

class MyFirebaseMessagingService :
    FirebaseMessagingService() {

    // Firebase Firestore
    private val firestore =
        FirebaseFirestore.getInstance()

    // Firebase Auth
    private val auth =
        FirebaseAuth.getInstance()

    /**
     * CALLED WHEN NEW FCM TOKEN GENERATED
     */
    override fun onNewToken(token: String) {

        super.onNewToken(token)

        Log.d(
            "FCM_TOKEN",
            "New Token: $token"
        )

        // Save token into Firestore
        saveTokenToFirestore(token)
    }

    /**
     * RECEIVE PUSH NOTIFICATION
     */
    override fun onMessageReceived(
        message: RemoteMessage
    ) {

        super.onMessageReceived(message)

        Log.d(
            "FCM_MESSAGE",
            "Message Received"
        )

        /**
         * LOG FULL DATA PAYLOAD
         */
        Log.d(
            "FCM_DATA",
            message.data.toString()
        )

        // Notification Title
        val title =
            message.notification?.title
                ?: "Emergency Alert"

        // Notification Body
        val body =
            message.notification?.body
                ?: "New blood request received"

        /**
         * SHOW NOTIFICATION USING HELPER
         */
        NotificationHelper(this)
           /* .showNotification(
                title = title,
                body = body
            )

            */

        /**
         * OPTIONAL:
         * LOCAL FALLBACK NOTIFICATION
         *
         * Uncomment if NotificationHelper fails.
         */


        showLocalNotification(
            title,
            body
        )

    }

    /**
     * SAVE TOKEN INTO FIRESTORE
     */
    private fun saveTokenToFirestore(
        token: String
    ) {

        val currentUser =
            auth.currentUser

        if (currentUser == null) {

            Log.d(
                "FCM_TOKEN",
                "User not logged in"
            )

            return
        }

        firestore.collection("donors")
            .document(currentUser.uid)
            .update(
                "fcmToken",
                token
            )
            .addOnSuccessListener {

                Log.d(
                    "FCM_TOKEN",
                    "Token saved successfully"
                )
            }
            .addOnFailureListener {

                Log.e(
                    "FCM_TOKEN",
                    "Failed to save token",
                    it
                )
            }
    }

    /**
     * OPTIONAL LOCAL NOTIFICATION METHOD
     *
     * Backup notification system
     */
    private fun showLocalNotification(
        title: String,
        body: String
    ) {

        val channelId =
            "vitadrop_channel"

        val notificationManager =
            getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        /**
         * CREATE CHANNEL
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel =
                NotificationChannel(
                    channelId,
                    "VitaDrop Notifications",
                    NotificationManager.IMPORTANCE_HIGH
                )

            notificationManager
                .createNotificationChannel(channel)
        }

        /**
         * BUILD NOTIFICATION
         */
        val notification =
            NotificationCompat.Builder(
                this,
                channelId
            )
                .setSmallIcon(
                    R.drawable.ic_launcher_foreground
                )
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(
                    NotificationCompat.PRIORITY_HIGH
                )
                .setAutoCancel(true)
                .build()

        notificationManager.notify(
            System.currentTimeMillis().toInt(),
            notification
        )
    }
}