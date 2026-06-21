package com.social.vitadrop.presentation.screens.common

/*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.social.vitadrop.presentation.viewmodel.RequestDetailsViewModel

@Composable
fun RequestDetailsScreen(
    requestId: String,
    viewModel: RequestDetailsViewModel
) {

    val request by viewModel
        .request
        .collectAsState()

    LaunchedEffect(requestId) {

        viewModel.loadRequest(
            requestId
        )
    }

    request?.let {

        RequestItemCard(
            request = it
        )
    }
}

*/



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.social.vitadrop.presentation.viewmodel.RequestDetailsViewModel

import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestDetailsScreen(
    requestId: String,
    viewModel: RequestDetailsViewModel,
    navController: NavController.Companion
) {

    val request by viewModel.request.collectAsState()
    val navController = rememberNavController()

    LaunchedEffect(requestId) {
        viewModel.loadRequest(requestId)
    }

    Scaffold(

        /* ================= TOP APP BAR ================= */
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
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
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

            when {

                /* ================= LOADING STATE ================= */
                request == null -> {

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        CircularProgressIndicator()

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Loading request details...",
                            color = Color.Gray
                        )
                    }
                }

                /* ================= SUCCESS STATE ================= */
                else -> {

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {

                        // Your original UI preserved exactly
                        RequestItemCard(
                            request = request!!
                        )
                    }
                }
            }
        }
    }
}