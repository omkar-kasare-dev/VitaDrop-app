package com.social.vitadrop.data.remote

/*

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

class FirebaseMessagingManager {

    /**
     * =====================================================
     * GET DEVICE FCM TOKEN
     * =====================================================
     */
    suspend fun getToken(): String {

        return FirebaseMessaging
            .getInstance()
            .token
            .await()
    }
}

 */

///


import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

class FirebaseMessagingManager {

    private val auth = FirebaseAuth.getInstance()

    private val firestore = FirebaseFirestore.getInstance()

    fun generateAndSaveToken() {

        val currentUser = auth.currentUser

        // USER NOT LOGGED IN
        if (currentUser == null) {

            Log.e(
                "FCM_TOKEN",
                "Current user is NULL"
            )

            return
        }

        FirebaseMessaging.getInstance()
            .token
            .addOnSuccessListener { token ->

                Log.d(
                    "FCM_TOKEN",
                    "Generated Token: $token"
                )

                saveTokenToFirestore(
                    userId = currentUser.uid,
                    token = token
                )
            }

            .addOnFailureListener {

                Log.e(
                    "FCM_TOKEN",
                    "Token generation failed: ${it.message}",
                    it
                )
            }
    }

    private fun saveTokenToFirestore(
        userId: String,
        token: String
    ) {

        firestore.collection("donors")
            .document(userId)
            .update(
                mapOf(
                    "fcmToken" to token
                )
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
                    "Failed to save token: ${it.message}",
                    it
                )
            }
    }

    //
    suspend fun getToken(): String {

        return try {

            val token = FirebaseMessaging
                .getInstance()
                .token
                .await()

            Log.d(
                "FCM_TOKEN",
                "Generated Token: $token"
            )

            token

        } catch (e: Exception) {

            Log.e(
                "FCM_TOKEN",
                "Error getting token: ${e.message}",
                e
            )

            ""
        }
    }
}