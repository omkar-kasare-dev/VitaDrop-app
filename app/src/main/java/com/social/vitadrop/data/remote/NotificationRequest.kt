package com.social.vitadrop.data.remote


data class NotificationRequest(
    val to: String,
    val notification: NotificationBody
)

data class NotificationBody(
    val title: String,
    val body: String
)