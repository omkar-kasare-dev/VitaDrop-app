package com.social.vitadrop.presentation.emergency.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.social.vitadrop.domain.model.RequestModel
import com.social.vitadrop.presentation.emergency.EmergencyEffect
import com.social.vitadrop.presentation.emergency.viewmodel.EmergencyViewModel

// EmergencyCardUI is now in this same package (presentation.emergency.screen), so it needs no import.

@Composable
fun EmergencyListScreen(
    requests: List<RequestModel>,
    emergencyViewModel: EmergencyViewModel,
    onViewContact: (String) -> Unit
) {
    val state by emergencyViewModel.state.collectAsState()
    val context = LocalContext.current

    // One-shot messages from the ViewModel
    LaunchedEffect(Unit) {
        emergencyViewModel.effect.collect { effect ->
            when (effect) {
                is EmergencyEffect.ShowMessage ->
                    Toast.makeText(context, effect.text, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        requests.forEach { request ->
            EmergencyCardUI(
                request = request,
                uiState = state,
                onIntent = emergencyViewModel::onIntent,
                onViewContact = onViewContact
            )
        }
    }
}