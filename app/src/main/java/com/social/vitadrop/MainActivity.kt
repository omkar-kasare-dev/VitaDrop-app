package com.social.vitadrop
/*
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.social.vitadrop.presentation.navigation.NavGraph
import com.social.vitadrop.ui.theme.VitaDropTheme

class MainActivity : ComponentActivity() {

    // Replaces ActivityCompat.requestPermissions(..., 101): that call needed an
    // onRequestPermissionsResult override that never existed, so the answer was ignored.
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* granted or not: nothing to do */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // The notification channel is now created in VitaDropApp (see Step 6), not here.
        requestNotificationPermission()

        setContent {
            VitaDropTheme {
                NavGraph()
            }
        }
    }

    // Android 13+ needs a runtime permission to show notifications
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        val granted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (!granted) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

 */

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.social.vitadrop.presentation.navigation.NavGraph
import com.social.vitadrop.ui.theme.VitaDropTheme
import com.social.vitadrop.utils.NotificationHelper

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* nothing to do */ }

    // The blood request a tapped notification is about (null when the app was opened normally)
    private var pendingRequestId by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermission()

        // Only on a fresh launch. After a rotation the same intent is still attached to the
        // activity, and reading it again would repeat the jump to the request.
        if (savedInstanceState == null) {
            pendingRequestId = intent?.getStringExtra(NotificationHelper.EXTRA_REQUEST_ID)
        }

        setContent {
            VitaDropTheme {
                NavGraph(
                    pendingRequestId = pendingRequestId,
                    onPendingRequestHandled = { pendingRequestId = null }
                )
            }
        }
    }

    // A notification tapped while this activity is already on screen
    // (if "overrides nothing" is shown in red, change the parameter to Intent?)
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        pendingRequestId = intent.getStringExtra(NotificationHelper.EXTRA_REQUEST_ID)
    }

    // Android 13+ needs a runtime permission to show notifications
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        val granted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (!granted) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}