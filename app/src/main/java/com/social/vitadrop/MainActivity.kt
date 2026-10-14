package com.social.vitadrop

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