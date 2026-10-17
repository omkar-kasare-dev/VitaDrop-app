package com.social.vitadrop.notification

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.social.vitadrop.data.repository.NotificationRepositoryImpl
import com.social.vitadrop.domain.model.UserRole
import com.social.vitadrop.domain.usecase.RegisterDeviceUseCase
import com.social.vitadrop.utils.NotificationHelper
import com.social.vitadrop.utils.SessionManager
import kotlinx.coroutines.runBlocking

/**
 * THE ONLY class in the project that extends FirebaseMessagingService.
 *
 * - If you already have one (any name), do not add a second: paste the two functions below
 *   into it, keep its name, and keep exactly ONE <service> entry in AndroidManifest.xml.
 *   With two services Android calls only one of them, and the other one's code never runs.
 * - If you have none, add this file and the <service> entry from the guide.
 */
class VitaDropMessagingService : FirebaseMessagingService() {

    /**
     * A push arrived. Android calls this:
     *  - ALWAYS when the app is in the foreground
     *  - when the app is closed, only for pushes that carry no "notification" part
     * (when the app is closed and the push has a "notification" part, Android shows it by itself)
     */
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title ?: message.data["title"].orEmpty()
        val body = message.notification?.body ?: message.data["body"].orEmpty()

        if (title.isBlank() && body.isBlank()) return

        NotificationHelper(applicationContext).showNotification(
            title = title,
            body = body,
            requestId = message.data["requestId"]
        )
    }

    /**
     * Firebase replaced this phone's token (reinstall, restore, long inactivity).
     * Without saving it, the server keeps pushing to the old token and nothing arrives.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        // Nobody is logged in on this phone: the token is saved at the next login.
        val role = UserRole.fromKey(SessionManager(applicationContext).getUserRole()) ?: return

        // onNewToken already runs on a background thread, so blocking here is fine.
        runBlocking {
            RegisterDeviceUseCase(NotificationRepositoryImpl()).invoke(role, knownToken = token)
        }
    }
}