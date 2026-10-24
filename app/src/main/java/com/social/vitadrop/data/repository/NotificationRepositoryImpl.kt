package com.social.vitadrop.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.social.vitadrop.domain.model.UserRole
import com.social.vitadrop.domain.repository.NotificationRepository
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.io.IOException

/**
 * Push token handling in ONE place.
 *
 * STEP 1: the token is saved in devices/{uid}, NOT in donors/{uid} or hospitals/{uid}.
 * Firestore rules let only the owner write it and let ONLY Cloud Functions read it,
 * so no other user can ever see anybody's token.
 *
 * (The role parameter is kept so callers and the interface do not change.)
 */
class NotificationRepositoryImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val messaging: FirebaseMessaging = FirebaseMessaging.getInstance()
) : NotificationRepository {

    override suspend fun registerDevice(role: UserRole, knownToken: String?): Result<Unit> =
        // NonCancellable: the login screen is closed right after login, but this must still finish.
        withContext(NonCancellable) {
            try {
                val uid = auth.currentUser?.uid
                    ?: return@withContext Result.failure<Unit>(Exception("Not logged in"))

                val token = knownToken
                    ?: withTimeoutOrNull(TIMEOUT_MS) { messaging.token.await() }
                    ?: return@withContext Result.failure<Unit>(IOException(NETWORK_MESSAGE))

                val saved = withTimeoutOrNull(TIMEOUT_MS) {
                    firestore.collection(DEVICES)
                        .document(uid)
                        .set(
                            mapOf(
                                "token" to token,
                                "platform" to "android",
                                "updatedAt" to FieldValue.serverTimestamp()
                            )
                        )
                        .await()
                    true
                }
                if (saved == null) {
                    Log.e(TAG, "Token NOT saved for $uid (timeout)")
                    return@withContext Result.failure<Unit>(IOException(NETWORK_MESSAGE))
                }

                Log.d(TAG, "Device token saved: devices/$uid")
                Result.success(Unit)

            } catch (e: Exception) {
                Log.e(TAG, "Device registration failed", e)
                Result.failure(e)
            }
        }

    /** Best effort. It never throws, so logout can always continue. */
    override suspend fun unregisterDevice(role: UserRole?) {
        withContext(NonCancellable) {
            val uid = auth.currentUser?.uid

            // 1) delete the token document (needs the user to still be signed in)
            if (uid != null) {
                try {
                    withTimeoutOrNull(SHORT_TIMEOUT_MS) {
                        firestore.collection(DEVICES).document(uid).delete().await()
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Could not remove the device token", e)
                }
            }

            // 2) invalidate the token on this device. A fresh one is created at the next login.
            try {
                withTimeoutOrNull(SHORT_TIMEOUT_MS) {
                    messaging.deleteToken().await()
                }
            } catch (e: Exception) {
                Log.w(TAG, "Could not delete the device token", e)
            }
        }
    }

    private companion object {
        const val TAG = "NOTIFICATION_REPO"
        const val DEVICES = "devices"
        const val TIMEOUT_MS = 15_000L
        const val SHORT_TIMEOUT_MS = 5_000L
        const val NETWORK_MESSAGE =
            "Could not reach the server. Check your internet connection and try again."
    }
}