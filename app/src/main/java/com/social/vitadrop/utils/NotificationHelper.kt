package com.social.vitadrop.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.social.vitadrop.MainActivity
import com.example.kotlinbasics.R
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class NotificationHelper(
    private val context: Context
) {

    companion object {

        const val CHANNEL_ID =
            "emergency_blood_requests"

        const val CHANNEL_NAME =
            "Emergency Blood Requests"
    }

    /**
     * CREATE NOTIFICATION CHANNEL
     */
    fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {

                description =
                    "Emergency blood request alerts"
            }

            val manager =
                context.getSystemService(
                    NotificationManager::class.java
                )

            manager.createNotificationChannel(channel)
        }
    }

    /**
     * SHOW NOTIFICATION
     */
    /*
    fun showNotification(
        title: String,
        body: String
    ) {

        val intent = Intent(
            context,
            MainActivity::class.java
        )

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        val pendingIntent =
            PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or
                        PendingIntent.FLAG_UPDATE_CURRENT
            )

        val builder =
            NotificationCompat.Builder(
                context,
                CHANNEL_ID
            )
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(
                    NotificationCompat.PRIORITY_HIGH
                )
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

        if (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            NotificationManagerCompat
                .from(context)
                .notify(
                    System.currentTimeMillis().toInt(),
                    builder.build()
                )
        }
    }*/
}