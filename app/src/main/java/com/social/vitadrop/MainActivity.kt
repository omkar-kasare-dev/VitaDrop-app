package com.social.vitadrop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.social.vitadrop.presentation.navigation.NavGraph
import com.social.vitadrop.ui.theme.VitaDropTheme
import com.social.vitadrop.utils.NotificationHelper

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {
   // val db = AppDatabase.getDatabase(applicationContext)
    //val dao = db.studentDao()
   // val repository = StudentRepository(dao)
   // val viewModel = StudentViewModel(repository)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // FCM Token Fetcher Code:
       FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
           if (!task.isSuccessful) {
               Log.e("FCM_TOKEN", "Token fetch failed", task.exception)
               return@addOnCompleteListener
           }

           val token = task.result
           Log.d("FCM_TOKEN", token)
       }
       //================
       //
        NotificationHelper(this)
           .createNotificationChannel()
        requestNotificationPermission()
        enableEdgeToEdge()
        setContent {
            VitaDropTheme {
                //viewModel.insertStudent("Omkar", "Android Development")
               // viewModel.students
               NavGraph()
            }
        }
    }


    //==================================================
    private fun requestNotificationPermission() {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU
        ) {

            if (
                ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.POST_NOTIFICATIONS
                    ),
                    101
                )
            }
        }
    }
}




