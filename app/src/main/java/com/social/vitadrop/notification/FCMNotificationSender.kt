package com.social.vitadrop.notification

import com.social.vitadrop.data.remote.NotificationBody
import com.social.vitadrop.data.remote.NotificationRequest
import com.social.vitadrop.data.remote.RetrofitInstance

class FCMNotificationSender {

    suspend fun sendNotification(
        token: String,
        title: String,
        body: String
    ) {

        try {

            val request =
                NotificationRequest(

                    to = token,

                    notification =
                        NotificationBody(
                            title = title,
                            body = body
                        )
                )

            RetrofitInstance.api
                .sendNotification(request)

        } catch (e: Exception) {

            e.printStackTrace()
        }
    }
}