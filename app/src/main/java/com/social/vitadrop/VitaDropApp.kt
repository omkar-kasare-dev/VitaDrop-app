package com.social.vitadrop

import android.app.Application

import com.social.vitadrop.utils.NotificationHelper

class VitaDropApp : Application() {

    override fun onCreate() {
        super.onCreate()
         // Should Be got Removed after the Testing
        // Configure Auth and Firestore emulators before
        // screens and repositories access Firebase.
       // FirebaseProvider.configure(this)

        // The channel must exist even when a push arrives while the app is closed and
        // MainActivity was never created. On Android 8+ a notification posted to a
        // channel that does not exist yet is dropped.
        NotificationHelper(this).createNotificationChannel()
    }
}