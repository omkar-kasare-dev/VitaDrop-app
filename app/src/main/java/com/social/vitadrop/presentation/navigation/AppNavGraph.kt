package com.social.vitadrop.presentation.navigation
/*
import LoginScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.social.vitadrop.utils.SessionManager

@Composable
fun AppNavGraph() {

    val context = LocalContext.current
    val session = SessionManager(context)

    val navController = rememberNavController()

    val startDestination = when (session.getUserRole()) {
        "patient" -> "dashboard/patient"
        "donor" -> "dashboard/donor"
        "hospital" -> "dashboard/hospital"
        "admin" -> "dashboard/admin"
        else -> "login"
    }

    NavHost(navController, startDestination = startDestination) {

        composable("login") { LoginScreen(navController) }

        composable("dashboard/patient") {
            PatientDashboardScreen(navController)
        }

        composable("dashboard/donor") {
            DonorDashboardScreen(navController)
        }

        composable("dashboard/hospital") {
            HospitalDashboardScreen(navController)
        }
    }
}


 */