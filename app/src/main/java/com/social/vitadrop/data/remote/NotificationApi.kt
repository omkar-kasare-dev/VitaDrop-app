package com.social.vitadrop.data.remote



import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationApi {

    @Headers(
        "Content-Type: application/json",
        "Authorization: key=YOUR_SERVER_KEY"
    )

    @POST("fcm/send")
    suspend fun sendNotification(
        @Body request: NotificationRequest
    ): Response<NotificationResponse>
}