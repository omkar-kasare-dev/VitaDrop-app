package com.social.vitadrop.presentation.dashboard.screen


import androidx.compose.runtime.*
import com.social.vitadrop.domain.model.UserRole
import com.social.vitadrop.presentation.emergency.viewmodel.EmergencyViewModel
import com.social.vitadrop.presentation.dashboard.DonorDashboardIntent
import com.social.vitadrop.presentation.dashboard.components.DonorDashboardContent
import com.social.vitadrop.presentation.dashboard.components.HospitalDashboardContent
import com.social.vitadrop.presentation.dashboard.viewmodel.DonorDashboardViewModel

@Composable
fun DonorDashboardScreen(
    role: UserRole,
    viewModel: DonorDashboardViewModel,
    emergencyViewModel: EmergencyViewModel,
    onProfileClick: () -> Unit,
    onDonorsClick: () -> Unit,
    onEmergencyClick: () -> Unit,
    onRequestBloodClick: () -> Unit,
    onChatClick: () -> Unit,
    onViewContact: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onIntent(DonorDashboardIntent.Refresh)
    }

    if (role == UserRole.HOSPITAL) {
        HospitalDashboardContent(
            state = state,
            emergencyViewModel = emergencyViewModel,
            onRefresh = { viewModel.onIntent(DonorDashboardIntent.Refresh) },
            onProfileClick = onProfileClick,
            onDonorsClick = onDonorsClick,
            onEmergencyClick = onEmergencyClick,
            onRequestBloodClick = onRequestBloodClick,
            onChatClick = onChatClick,
            onViewContact = onViewContact
        )
    } else {
        DonorDashboardContent(
            state = state,
            emergencyViewModel = emergencyViewModel,
            onRefresh = { viewModel.onIntent(DonorDashboardIntent.Refresh) },
            onProfileClick = onProfileClick,
            onEmergencyClick = onEmergencyClick,
            onChatClick = onChatClick,
            onViewContact = onViewContact
        )
    }
}

