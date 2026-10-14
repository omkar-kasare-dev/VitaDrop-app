package com.social.vitadrop.presentation.request.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.social.vitadrop.presentation.request.details.RequestDetailsIntent
import com.social.vitadrop.presentation.request.details.RequestDetailsState
import com.social.vitadrop.presentation.request.viewmodel.RequestDetailsViewModel
import com.social.vitadrop.presentation.request.common.RequestItemCard

// Real-World Design Palette Constants
private val AppBgColor = Color(0xFFF8FAFC)
private val CardBgColor = Color(0xFFFFFFFF)
private val TextPrimary = Color(0xFF0F172A)
private val TextSecondary = Color(0xFF64748B)
private val PrimaryAccent = Color(0xFFE11D48)

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
        containerColor = AppBgColor,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Request Details",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Patient medical information",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CardBgColor
                )
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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
                        CircularProgressIndicator(
                            color = PrimaryAccent,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(28.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Loading request details...",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
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
                        Card(
                            modifier = Modifier.fillMaxSize(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                            border = BorderStroke(1.dp, Color(0xFFFCA5A5))
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(20.dp)
                                    .fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = error,
                                    color = PrimaryAccent,
                                    fontSize = 12.sp
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Button(
                                    onClick = onRetry,
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent)
                                ) {
                                    Text("Retry", fontSize = 12.sp, color = Color.White)
                                }
                            }
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