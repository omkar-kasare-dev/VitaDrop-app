package com.social.vitadrop.presentation.notification



import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.kotlinbasics.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.social.vitadrop.utils.NotificationHelper



class MyFirebaseMessagingService :
    FirebaseMessagingService() {

    /**
     * =====================================================
     * NEW TOKEN GENERATED
     * =====================================================
     */
    override fun onNewToken(token: String) {

        super.onNewToken(token)

        android.util.Log.d(
            "FCM_TOKEN",
            token
        )
    }

    /**
     * =====================================================
     * RECEIVE MESSAGE
     * =====================================================
     */
    /*
    override fun onMessageReceived(
        message: RemoteMessage
    ) {

        super.onMessageReceived(message)

        val title =
            message.notification?.title
                ?: "Emergency Alert"

        val body =
            message.notification?.body
                ?: "New emergency request"

        showNotification(
            title,
            body
        )
    }

     */

    // Modified onMessageReceived.
    override fun onMessageReceived(
        message: RemoteMessage
    ) {

        super.onMessageReceived(message)

        Log.d(
            "FCM_MESSAGE",
            "Message Received"
        )

        val title =
            message.notification?.title
                ?: "Emergency Request"

        val body =
            message.notification?.body
                ?: "New blood request arrived"

        NotificationHelper(this)
            .showNotification(
                title = title,
                body = body
            )
    }

    /**
     * =====================================================
     * SHOW LOCAL NOTIFICATION
     * =====================================================
     */
    private fun showNotification(
        title: String,
        body: String
    ) {

        val channelId =
            "emergency_channel"

        val notificationManager =
            getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        /**
         * =================================================
         * CREATE CHANNEL
         * =================================================
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel =
                NotificationChannel(
                    channelId,
                    "Emergency Notifications",
                    NotificationManager.IMPORTANCE_HIGH
                )

            notificationManager
                .createNotificationChannel(channel)
        }

        /**
         * =================================================
         * BUILD NOTIFICATION
         * =================================================
         */
        val notification =
            NotificationCompat.Builder(
                this,
                channelId
            )
                .setSmallIcon(
                    R.drawable.ic_notification
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


    // GetToken Suspend Function.

}