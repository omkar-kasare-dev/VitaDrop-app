package com.social.vitadrop.data.repository
/*
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.social.vitadrop.data.mapper.collectionName
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
 * Fixes compared to the old FirebaseMessagingManager:
 *  - hospitals now get their token saved too (the old code always wrote to donors/{uid},
 *    which does not exist for a hospital, so the update silently failed)
 *  - only donors are subscribed to the "donors" topic (before: every user was)
 *  - the token is no longer printed in Logcat
 *  - logging out removes the token, so a device never keeps receiving a previous user's pushes
 */
class NotificationRepositoryImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val messaging: FirebaseMessaging = FirebaseMessaging.getInstance()
) : NotificationRepository {

    override suspend fun registerDevice(role: UserRole, knownToken: String?): Result<Unit> =
    // NonCancellable: the login screen (and its ViewModel) is closed right after login,
        // but the registration must still finish.
        withContext(NonCancellable) {
            try {
                val uid = auth.currentUser?.uid
                    ?: return@withContext Result.failure<Unit>(Exception("Not logged in"))

                val token = knownToken
                    ?: withTimeoutOrNull(TIMEOUT_MS) { messaging.token.await() }
                    ?: return@withContext Result.failure<Unit>(IOException(NETWORK_MESSAGE))

                val saved = withTimeoutOrNull(TIMEOUT_MS) {
                    firestore.collection(role.collectionName)
                        .document(uid)
                        .update("fcmToken", token)
                        .await()
                    true
                }
                if (saved == null) {
                    return@withContext Result.failure<Unit>(IOException(NETWORK_MESSAGE))
                }

                // Compatibility: the existing sender still targets the "donors" topic.
                // Donors only. Remove this when notifications are sent to individual tokens.
                if (role == UserRole.DONOR) {
                    try {
                        withTimeoutOrNull(TIMEOUT_MS) {
                            messaging.subscribeToTopic(TOPIC_DONORS).await()
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Topic subscription failed", e)
                    }
                }

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

            // 1) remove the token from the profile (needs the user to still be signed in)
            if (uid != null && role != null) {
                try {
                    withTimeoutOrNull(SHORT_TIMEOUT_MS) {
                        firestore.collection(role.collectionName)
                            .document(uid)
                            .update("fcmToken", FieldValue.delete())
                            .await()
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Could not remove token from profile", e)
                }
            }

            // 2) invalidate the token on this device (also ends its topic subscriptions).
            //    A fresh token is created automatically at the next login.
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
        const val TOPIC_DONORS = "donors"
        const val TIMEOUT_MS = 15_000L
        const val SHORT_TIMEOUT_MS = 5_000L
        const val NETWORK_MESSAGE =
            "Could not reach the server. Check your internet connection and try again."
    }
}

 */


import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.social.vitadrop.data.mapper.collectionName
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
 * Fixes compared to the old FirebaseMessagingManager:
 *  - hospitals now get their token saved too (the old code always wrote to donors/{uid},
 *    which does not exist for a hospital, so the update silently failed)
 *  - only donors are subscribed to the "donors" topic (before: every user was)
 *  - the token is no longer printed in Logcat
 *  - logging out removes the token, so a device never keeps receiving a previous user's pushes
 */
class NotificationRepositoryImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val messaging: FirebaseMessaging = FirebaseMessaging.getInstance()
) : NotificationRepository {

    override suspend fun registerDevice(role: UserRole, knownToken: String?): Result<Unit> =
    // NonCancellable: the login screen (and its ViewModel) is closed right after login,
        // but the registration must still finish.
        withContext(NonCancellable) {
            try {
                val uid = auth.currentUser?.uid
                    ?: return@withContext Result.failure<Unit>(Exception("Not logged in"))

                val token = knownToken
                    ?: withTimeoutOrNull(TIMEOUT_MS) { messaging.token.await() }
                    ?: return@withContext Result.failure<Unit>(IOException(NETWORK_MESSAGE))

                val saved = withTimeoutOrNull(TIMEOUT_MS) {
                    firestore.collection(role.collectionName)
                        .document(uid)
                        .update("fcmToken", token)
                        .await()
                    true
                }
                if (saved == null) {
                    return@withContext Result.failure<Unit>(IOException(NETWORK_MESSAGE))
                }

                // No topic subscription: the Cloud Functions send to each donor's own token
                // (donors/{uid}.fcmToken), so a "donors" topic would only add one more thing to fail.
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

            // 1) remove the token from the profile (needs the user to still be signed in)
            if (uid != null && role != null) {
                try {
                    withTimeoutOrNull(SHORT_TIMEOUT_MS) {
                        firestore.collection(role.collectionName)
                            .document(uid)
                            .update("fcmToken", FieldValue.delete())
                            .await()
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Could not remove token from profile", e)
                }
            }

            // 2) invalidate the token on this device (also ends its topic subscriptions).
            //    A fresh token is created automatically at the next login.
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
        const val TIMEOUT_MS = 15_000L
        const val SHORT_TIMEOUT_MS = 5_000L
        const val NETWORK_MESSAGE =
            "Could not reach the server. Check your internet connection and try again."
    }
}