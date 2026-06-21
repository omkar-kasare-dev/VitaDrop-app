package com.social.vitadrop.presentation.navigation


import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import com.social.vitadrop.data.remote.FirebaseAuthService
import com.social.vitadrop.data.repository.AuthRepositoryImpl
import com.social.vitadrop.data.repository.DashboardRepositoryImpl
import com.social.vitadrop.data.repository.DonorRepositoryImpl
import com.social.vitadrop.data.repository.RequestRepositoryImpl
import com.social.vitadrop.data.repository.ResponseRepositoryImpl
import com.social.vitadrop.domain.usecase.RegisterUserUseCase
import com.social.vitadrop.presentation.screens.auth.LoginScreen

import com.social.vitadrop.presentation.screens.auth.RegisterScreen
import com.social.vitadrop.presentation.screens.common.ChatScreen
import com.social.vitadrop.presentation.screens.common.ProfileScreen
import com.social.vitadrop.presentation.screens.common.RequestBloodScreen
import com.social.vitadrop.presentation.screens.common.RequestDetailsScreen
import com.social.vitadrop.presentation.screens.common.RequestListScreen

import com.social.vitadrop.presentation.screens.donor.DonorDashboardScreen
import com.social.vitadrop.presentation.screens.donor.DonorListScreen
import com.social.vitadrop.presentation.splash.SplashScreen

import com.social.vitadrop.presentation.viewmodel.AuthViewModel
import com.social.vitadrop.presentation.viewmodel.AuthViewModelFactory
import com.social.vitadrop.presentation.viewmodel.ProfileViewModel
import com.social.vitadrop.presentation.viewmodel.RegisterViewModel

import com.social.vitadrop.presentation.viewmodel.DonorDashboardViewModel
import com.social.vitadrop.presentation.viewmodel.DonorViewModel
import com.social.vitadrop.presentation.viewmodel.EmergencyViewModel
import com.social.vitadrop.presentation.viewmodel.RequestDetailsViewModel
import com.social.vitadrop.presentation.viewmodel.RequestViewModel

import com.social.vitadrop.utils.SessionManager

@Composable
fun NavGraph(modifier: Modifier = Modifier) {

    val navController = rememberNavController()
    val context = LocalContext.current
    val session = SessionManager(context)

    //  ROLE BASED START DESTINATION
    val startDestination = when (session.getUserRole()) {
        "donor" -> "dashboard/donor"
        "hospital" -> "dashboard/hospital"
        "admin" -> "dashboard/admin"
        "patient" -> "dashboard/patient"
        else -> "login"
    }

    NavHost(
        navController = navController,
        startDestination = "splash", // keep splash as entry
        modifier = modifier
    ) {

        //SPLASH
        composable("splash") {
            SplashScreen(navController)
        }

        // LOGIN
        composable("login") {

            val viewModel: AuthViewModel = viewModel(
                factory = AuthViewModelFactory(context)
            )

            LoginScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        // REGISTER
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

        //  ROLE-BASED DASHBOARD
        composable("dashboard/{role}") { backStackEntry ->

            val role = backStackEntry.arguments?.getString("role")

            when (role) {

                // DONOR DASHBOARD (FIXED)
                "donor" -> {

                    val donorViewModel: DonorDashboardViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return DonorDashboardViewModel(
                                    repository = DashboardRepositoryImpl()
                                ) as T
                            }
                        }
                    )
                    //
                    val emergencyViewModel: EmergencyViewModel =
                        viewModel(
                            factory = object :
                                ViewModelProvider.Factory {

                                @Suppress("UNCHECKED_CAST")
                                override fun <T : ViewModel> create(
                                    modelClass: Class<T>
                                ): T {

                                    return EmergencyViewModel(
                                        repository =
                                            ResponseRepositoryImpl(
                                                FirebaseFirestore
                                                    .getInstance()
                                            )
                                    ) as T
                                }
                            }
                        )


                    DonorDashboardScreen(

                        navController = navController,

                        viewModel = donorViewModel,
                        emergencyViewModel =
                            emergencyViewModel
                    )
                }

                // OTHER ROLES (UNCHANGED)
                "hospital" -> {

                    val donorViewModel: DonorDashboardViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return DonorDashboardViewModel(
                                    repository = DashboardRepositoryImpl()
                                ) as T
                            }
                        }
                    )
                    //
                    val emergencyViewModel: EmergencyViewModel =
                        viewModel(
                            factory = object :
                                ViewModelProvider.Factory {

                                @Suppress("UNCHECKED_CAST")
                                override fun <T : ViewModel> create(
                                    modelClass: Class<T>
                                ): T {

                                    return EmergencyViewModel(
                                        repository =
                                            ResponseRepositoryImpl(
                                                FirebaseFirestore
                                                    .getInstance()
                                            )
                                    ) as T
                                }
                            }
                        )



                    DonorDashboardScreen(

                        navController = navController,

                        viewModel = donorViewModel,
                        emergencyViewModel =
                            emergencyViewModel

                    )
                }


               // "admin" -> DashboardScreen(navController)

               // "patient" -> DashboardScreen(navController)

                else -> {

                    val donorViewModel: DonorDashboardViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return DonorDashboardViewModel(
                                    repository = DashboardRepositoryImpl()
                                ) as T
                            }
                        }
                    )
                    //
                    val emergencyViewModel: EmergencyViewModel =
                        viewModel(
                            factory = object :
                                ViewModelProvider.Factory {

                                @Suppress("UNCHECKED_CAST")
                                override fun <T : ViewModel> create(
                                    modelClass: Class<T>
                                ): T {

                                    return EmergencyViewModel(
                                        repository =
                                            ResponseRepositoryImpl(
                                                FirebaseFirestore
                                                    .getInstance()
                                            )
                                    ) as T
                                }
                            }
                        )



                    DonorDashboardScreen(
                        navController = navController,
                        viewModel = donorViewModel,
                        emergencyViewModel =
                            emergencyViewModel
                    )


                }
            }
        }

        // PROFILE
        composable("profile") {

            val sessionManager = remember { SessionManager(context) }

            val profileViewModel = remember {
                ProfileViewModel(
                    db = FirebaseFirestore.getInstance(),
                    auth = FirebaseAuth.getInstance()
                )
            }

            ProfileScreen(
                navController = navController,
                viewModel = profileViewModel,
                sessionManager = sessionManager
            )
        }


        //
        composable("requestBlood") {

            val requestViewModel = remember {
                RequestViewModel(
                    repository = RequestRepositoryImpl()
                )
            }

            RequestBloodScreen(
                navController = navController,
                viewModel = requestViewModel
            )
        }

        composable("request_list") {

            val donorDashboardViewModel = remember {
                DonorDashboardViewModel(
                    repository = DashboardRepositoryImpl()
                )
            }

            RequestListScreen(navController = navController,
                viewModel = donorDashboardViewModel
            )
        }

        composable("donors_list") {

            val donorDashboardViewModel = remember {
                DonorViewModel(
                    repository = DonorRepositoryImpl()
                )
            }

            DonorListScreen(navController = navController,
                viewModel = donorDashboardViewModel
            )
        }

        // AI Assistant
        composable(
            route = "chat_assistant"
        ) {

            ChatScreen(
                navController = navController
            )
        }

        composable(
            route = "request_details/{requestId}"
        ) { backStackEntry ->

            val requestId =
                backStackEntry.arguments
                    ?.getString("requestId")
                    ?: ""

            val requestDetailsViewModel:
                    RequestDetailsViewModel =
                viewModel(
                    factory = object : ViewModelProvider.Factory {

                        override fun <T : ViewModel> create(
                            modelClass: Class<T>
                        ): T {

                            return RequestDetailsViewModel(

                                repository =
                                    ResponseRepositoryImpl(
                                        FirebaseFirestore.getInstance()
                                    )

                            ) as T
                        }
                    }
                )
            val navController = rememberNavController()


            RequestDetailsScreen(
                requestId = requestId,
                viewModel = requestDetailsViewModel,
                navController = NavController

            )
        }

    }
}