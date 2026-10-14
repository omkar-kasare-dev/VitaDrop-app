package com.social.vitadrop.presentation.navigation


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore

import com.social.vitadrop.data.remote.FirebaseAuthService
import com.social.vitadrop.data.repository.AuthRepositoryImpl
import com.social.vitadrop.data.repository.DashboardRepositoryImpl
import com.social.vitadrop.data.repository.DonorRepositoryImpl
import com.social.vitadrop.data.repository.RequestRepositoryImpl
import com.social.vitadrop.data.repository.ResponseRepositoryImpl
import com.social.vitadrop.domain.usecase.LoginUseCase
import com.social.vitadrop.domain.usecase.RegisterUserUseCase
import com.social.vitadrop.presentation.auth.login.screen.LoginScreen
import com.social.vitadrop.presentation.auth.login.viewmodel.LoginViewModel
import com.social.vitadrop.presentation.auth.register.screen.RegisterScreen
import com.social.vitadrop.presentation.auth.register.viewmodel.RegisterViewModel


import com.social.vitadrop.presentation.screens.common.ChatScreen

import com.social.vitadrop.presentation.splash.SplashScreen

import com.social.vitadrop.domain.usecase.CreateRequestUseCase
import com.social.vitadrop.presentation.request.screen.CreateRequestScreen

import com.social.vitadrop.presentation.request.viewmodel.CreateRequestViewModel

import com.social.vitadrop.utils.SessionManager

import com.social.vitadrop.domain.usecase.GetDonorsUseCase
import com.social.vitadrop.domain.usecase.GetRequestByIdUseCase
import com.social.vitadrop.domain.usecase.HasAlreadyRespondedUseCase
import com.social.vitadrop.domain.usecase.ObserveResponseCountUseCase
import com.social.vitadrop.domain.usecase.RespondToRequestUseCase
import com.social.vitadrop.presentation.donor.screen.DonorListScreen

import com.social.vitadrop.presentation.donor.viewmodel.DonorListViewModel
import com.social.vitadrop.presentation.emergency.viewmodel.EmergencyViewModel
import com.social.vitadrop.presentation.request.screen.RequestDetailsScreen
import com.social.vitadrop.presentation.request.viewmodel.RequestDetailsViewModel

import com.social.vitadrop.domain.model.UserRole
import com.social.vitadrop.domain.usecase.GetDashboardUseCase
import com.social.vitadrop.domain.usecase.GetOpenRequestsUseCase
import com.social.vitadrop.presentation.dashboard.screen.DonorDashboardScreen
import com.social.vitadrop.presentation.dashboard.viewmodel.DonorDashboardViewModel
import com.social.vitadrop.presentation.request.list.RequestListScreen
import com.social.vitadrop.presentation.request.list.RequestListViewModel

import com.social.vitadrop.data.repository.NotificationRepositoryImpl
import com.social.vitadrop.data.repository.ProfileRepositoryImpl
import com.social.vitadrop.domain.usecase.GetProfileUseCase
import com.social.vitadrop.domain.usecase.LogoutUseCase
import com.social.vitadrop.domain.usecase.RegisterDeviceUseCase
import com.social.vitadrop.presentation.profile.ProfileScreen
import com.social.vitadrop.presentation.profile.ProfileViewModel


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

            val loginViewModel: LoginViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        LoginViewModel(
                            loginUseCase = LoginUseCase(AuthRepositoryImpl(FirebaseAuthService())),
                            sessionManager = SessionManager(context.applicationContext),
                            registerDevice = RegisterDeviceUseCase(NotificationRepositoryImpl())
                        )
                    }
                }
            )

            LoginScreen(
                viewModel = loginViewModel,
                onNavigateToRegister = { navController.navigate("register") },
                onLoggedIn = { role ->
                    navController.navigate("dashboard/${role.key}") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // REGISTER
        composable("register") {

            val registerViewModel: RegisterViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        RegisterViewModel(
                            RegisterUserUseCase(
                                AuthRepositoryImpl(FirebaseAuthService())
                            )
                        )
                    }
                }
            )

            RegisterScreen(
                viewModel = registerViewModel,
                onNavigateToLogin = { navController.navigate("login") },
                onRegistered = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }


        composable("dashboard/{role}") { backStackEntry ->

            // Unknown roles (for example an old saved "admin" session) fall back to the donor view, as before
            val role = UserRole.fromKey(backStackEntry.arguments?.getString("role").orEmpty())
                ?: UserRole.DONOR

            val dashboardViewModel: DonorDashboardViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        DonorDashboardViewModel(GetDashboardUseCase(DashboardRepositoryImpl()))
                    }
                }
            )

            val emergencyViewModel = rememberEmergencyViewModel()

            DonorDashboardScreen(
                role = role,
                viewModel = dashboardViewModel,
                emergencyViewModel = emergencyViewModel,
                onProfileClick = { navController.navigate("profile") },
                onDonorsClick = { navController.navigate("donors_list") },
                onEmergencyClick = { navController.navigate("request_list") },
                onRequestBloodClick = { navController.navigate("requestBlood") },
                onChatClick = { navController.navigate("chat_assistant") },
                onViewContact = { requestId -> navController.navigate("request_details/$requestId") }
            )
        }

        // PROFILE
        composable("profile") {

            val profileViewModel: ProfileViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        ProfileViewModel(
                            getProfile = GetProfileUseCase(ProfileRepositoryImpl()),
                            logout = LogoutUseCase(
                                authRepository = AuthRepositoryImpl(FirebaseAuthService()),
                                notificationRepository = NotificationRepositoryImpl()
                            ),
                            sessionManager = SessionManager(context.applicationContext)
                        )
                    }
                }
            )

            ProfileScreen(
                viewModel = profileViewModel,
                onBack = { navController.popBackStack() },
                onLoggedOut = { navController.navigate("login") { popUpTo(0) } }
            )
        }


        //
        composable("requestBlood") {

            val createRequestViewModel: CreateRequestViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        CreateRequestViewModel(
                            CreateRequestUseCase(RequestRepositoryImpl())
                        )
                    }
                }
            )

            CreateRequestScreen(
                viewModel = createRequestViewModel,
                onBack = { navController.popBackStack() },
                onRequestCreated = { navController.popBackStack() }
            )
        }
/* Main Request list Route
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

 */
        composable("request_list") {

            val requestListViewModel: RequestListViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        RequestListViewModel(GetOpenRequestsUseCase(DashboardRepositoryImpl()))
                    }
                }
            )

            RequestListScreen(
                viewModel = requestListViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("donors_list") {

            val donorListViewModel: DonorListViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        DonorListViewModel(
                            GetDonorsUseCase(DonorRepositoryImpl())
                        )
                    }
                }
            )

            DonorListScreen(
                viewModel = donorListViewModel,
                onBack = { navController.popBackStack() }
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

        composable(route = "request_details/{requestId}") { backStackEntry ->

            val requestId = backStackEntry.arguments?.getString("requestId").orEmpty()

            val detailsViewModel: RequestDetailsViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        RequestDetailsViewModel(
                            requestId = requestId,
                            getRequestById = GetRequestByIdUseCase(
                                ResponseRepositoryImpl(FirebaseFirestore.getInstance())
                            )
                        )
                    }
                }
            )

            RequestDetailsScreen(
                viewModel = detailsViewModel,
                onBack = { navController.popBackStack() }
            )
        }

    }
}

@Composable
private fun rememberEmergencyViewModel(): EmergencyViewModel =
    viewModel(
        factory = viewModelFactory {
            initializer {
                val repository = ResponseRepositoryImpl(FirebaseFirestore.getInstance())
                EmergencyViewModel(
                    respondToRequest = RespondToRequestUseCase(repository),
                    hasAlreadyResponded = HasAlreadyRespondedUseCase(repository),
                    observeResponseCount = ObserveResponseCountUseCase(repository)
                )
            }
        }
    )