package com.social.vitadrop.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
// IMPORTANT: use the R class of your app "namespace" (see app/build.gradle).
// If your namespace is com.social.vitadrop, change this to: import com.social.vitadrop.R
import com.example.kotlinbasics.R
import com.social.vitadrop.MainActivity

class NotificationHelper(private val context: Context) {

    companion object {
        // Must be EXACTLY the same as NOTIFICATION_CHANNEL_ID in functions/utils/config.js,
        // because the server tells Android which channel to use for background pushes.
        const val CHANNEL_ID = "emergency_blood_requests"
        private const val CHANNEL_NAME = "Emergency Blood Requests"

        // Same key as the "requestId" value the server puts in the push data,
        // so a tap on a background push and a tap on this notification carry the same extra.
        const val EXTRA_REQUEST_ID = "requestId"
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications about emergency blood requests"
            }

            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            manager.createNotificationChannel(channel)
        }
    }

    /**
     * Shows a notification. Called by VitaDropMessagingService when a push arrives while
     * the app is open (when the app is closed, Android shows the push by itself).
     *
     * @param requestId the blood request the push is about (null for other pushes)
     */
    fun showNotification(title: String, body: String, requestId: String? = null) {
        createNotificationChannel()

        // Android 13 and later require notification permission.
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // One notification per request: a second push for the same request replaces the first
        // instead of stacking. Before, the id was always different.
        val notificationId = requestId?.hashCode() ?: System.currentTimeMillis().toInt()

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            requestId?.let { putExtra(EXTRA_REQUEST_ID, it) }
        }

        // The request code must differ per notification. Before it was always 0 with
        // FLAG_UPDATE_CURRENT, so every notification shared (and overwrote) one PendingIntent.
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title.ifBlank { "VitaDrop" })
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setDefaults(NotificationCompat.DEFAULT_ALL)   // sound + vibration on Android 7 and older
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context)
            .notify(notificationId, notification)
    }
}