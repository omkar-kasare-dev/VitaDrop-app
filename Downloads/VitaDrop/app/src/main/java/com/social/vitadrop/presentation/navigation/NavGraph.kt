package com.social.vitadrop.presentation.navigation

import LoginScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import com.social.vitadrop.data.remote.FirebaseAuthService
import com.social.vitadrop.data.repository.AuthRepositoryImpl
import com.social.vitadrop.domain.usecase.RegisterUserUseCase

import com.social.vitadrop.presentation.screens.auth.RegisterScreen
import com.social.vitadrop.presentation.screens.common.ProfileScreen
import com.social.vitadrop.presentation.screens.donor.DashboardScreen
import com.social.vitadrop.presentation.splash.SplashScreen

import com.social.vitadrop.presentation.viewmodel.AuthViewModel
import com.social.vitadrop.presentation.viewmodel.AuthViewModelFactory
import com.social.vitadrop.presentation.viewmodel.ProfileViewModel
import com.social.vitadrop.presentation.viewmodel.RegisterViewModel

import com.social.vitadrop.utils.SessionManager

@Composable
fun NavGraph(modifier: Modifier = Modifier) {

    val navController = rememberNavController()
    val context = LocalContext.current
    val session = SessionManager(context)



    // ✅ SAFE ROLE HANDLING
    val startDestination = when (session.getUserRole()) {
        "donor" -> "dashboard/donor"
        "hospital" -> "dashboard/hospital"
        "admin" -> "dashboard/admin"
        "patient" -> "dashboard/patient"
        else -> "login"
    }

    NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = modifier
    ) {
        composable("splash") {
            SplashScreen(navController)
        }

        // 🔐 LOGIN SCREEN
        composable("login") {

            val viewModel: AuthViewModel = viewModel(
                factory = AuthViewModelFactory(context)
            )

            LoginScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        // 📝 REGISTER SCREEN
        composable("register") {

            val registerViewModel = remember {
                RegisterViewModel(
                    RegisterUserUseCase(
                        AuthRepositoryImpl(
                            FirebaseAuthService()
                        )
                    )
                )
            }

            RegisterScreen(
                navController = navController,
                viewModel = registerViewModel
            )
        }

        // 📊 ROLE-BASED DASHBOARD
        composable("dashboard/{role}") { backStackEntry ->

            val role = backStackEntry.arguments?.getString("role")

            when (role) {

                "donor" -> DashboardScreen(navController)

                "hospital" -> DashboardScreen(navController)

                "admin" -> DashboardScreen(navController)

                "patient" -> DashboardScreen(navController)

                else -> DashboardScreen(navController)
            }
        }

        // 👤 PROFILE SCREEN
        composable("profile") {
            val context = LocalContext.current
            val sessionManager = remember { SessionManager(context) }

            val profileViewModel = remember {
                ProfileViewModel(
                    db = FirebaseFirestore.getInstance(),
                    auth = FirebaseAuth.getInstance()
                )
            }
            ProfileScreen(navController = navController,
                viewModel = profileViewModel,
                sessionManager = sessionManager)
        }

    }
}