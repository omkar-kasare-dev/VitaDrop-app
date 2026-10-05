package com.social.vitadrop.presentation.screens.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import com.social.vitadrop.domain.model.RequestModel
import com.social.vitadrop.presentation.viewmodel.EmergencyViewModel

@Composable
fun EmergencyListScreen(
    requests: List<RequestModel>,
    emergencyViewModel: EmergencyViewModel,
    onViewContact: (String) -> Unit
) {
    val uiState by emergencyViewModel
        .uiState
        .collectAsState()

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        requests.forEach { request ->
            EmergencyCardUI(
                request = request,
                uiState = uiState,
                onEvent = emergencyViewModel::onEvent,
                onViewContact = onViewContact
            )
        }
    }
}