package com.social.vitadrop.presentation.request.screen


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.social.vitadrop.presentation.request.details.RequestDetailsIntent
import com.social.vitadrop.presentation.request.details.RequestDetailsState
import com.social.vitadrop.presentation.request.viewmodel.RequestDetailsViewModel
// RequestItemCard still lives in screens.common; it is migrated together with the dashboard UI.
import com.social.vitadrop.presentation.screens.common.RequestItemCard

// =====================================================================
// STATEFUL WRAPPER
// =====================================================================
@Composable
fun RequestDetailsScreen(
    viewModel: RequestDetailsViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    RequestDetailsContent(
        state = state,
        onRetry = { viewModel.onIntent(RequestDetailsIntent.Retry) },
        onBack = onBack
    )
}

// =====================================================================
// STATELESS UI
// =====================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RequestDetailsContent(
    state: RequestDetailsState,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Request Details",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Patient medical information",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                },
                navigationIcon = {
                    // Before: this popped a brand-new NavController, so Back did nothing.
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF6F8FB))
        ) {
            val request = state.request
            val error = state.errorMessage

            when {
                // LOADING
                state.isLoading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(text = "Loading request details...", color = Color.Gray)
                    }
                }

                // ERROR + RETRY
                error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = error, color = Color.Red)

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = onRetry,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                        ) {
                            Text("Retry")
                        }
                    }
                }

                // SUCCESS
                request != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        RequestItemCard(request = request)
                    }
                }
            }
        }
    }
}