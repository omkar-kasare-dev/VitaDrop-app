package com.social.vitadrop.presentation.emergency.screen
/*
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
import com.social.vitadrop.domain.model.UserRole
import com.social.vitadrop.presentation.emergency.EmergencyEffect
import com.social.vitadrop.presentation.emergency.viewmodel.EmergencyViewModel

@Composable
fun EmergencyListScreen(
    requests: List<RequestModel>,
    emergencyViewModel: EmergencyViewModel,
    role: UserRole = UserRole.DONOR,
    onViewContact: (String) -> Unit
) {
    val state by emergencyViewModel.state.collectAsState()
    val context = LocalContext.current

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
                role = role,
                onIntent = emergencyViewModel::onIntent,
                onViewContact = onViewContact
            )
        }
    }
}

 */


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
import com.social.vitadrop.domain.model.UserRole
import com.social.vitadrop.presentation.emergency.EmergencyEffect
import com.social.vitadrop.presentation.emergency.viewmodel.EmergencyViewModel

@Composable
fun EmergencyListScreen(
    requests: List<RequestModel>,
    emergencyViewModel: EmergencyViewModel,
    role: UserRole = UserRole.DONOR,
    onViewContact: (String) -> Unit
) {
    val state by emergencyViewModel.state.collectAsState()
    val myResponses by emergencyViewModel.myResponses.collectAsState()
    val context = LocalContext.current

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
                role = role,
                onIntent = emergencyViewModel::onIntent,
                onViewContact = onViewContact,
                // STEP 4: the donor's answer and what the hospital recorded
                myResponse = myResponses[request.requestId],
                responseChecked = request.requestId in myResponses,
                onDecline = { emergencyViewModel.decline(request.requestId) }
            )
        }
    }
}