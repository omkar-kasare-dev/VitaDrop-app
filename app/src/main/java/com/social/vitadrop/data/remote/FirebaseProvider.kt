package com.social.vitadrop.data.remote

import android.app.Application
import android.content.pm.ApplicationInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Configures Firebase Emulator connections for debug builds only.
 *
 * Physical devices must use the computer's Wi-Fi IPv4 address,
 * not localhost or 0.0.0.0.
 */
object FirebaseProvider {

    // Replace this value with your computer's actual Wi-Fi IPv4 address.
    private const val EMULATOR_HOST = "10.61.189.225"

    fun configure(application: Application) {
        val isDebuggable =
            application.applicationInfo.flags and
                    ApplicationInfo.FLAG_DEBUGGABLE != 0

        // Release builds continue using production Firebase.
        if (!isDebuggable) return

        FirebaseAuth.getInstance()
            .useEmulator(EMULATOR_HOST, 9099)

        FirebaseFirestore.getInstance()
            .useEmulator(EMULATOR_HOST, 8080)
    }
}


