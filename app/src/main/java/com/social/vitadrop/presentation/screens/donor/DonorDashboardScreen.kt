package com.social.vitadrop.presentation.screens.donor

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Adb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.social.vitadrop.presentation.event.DonorDashboardEvent
import com.social.vitadrop.presentation.screens.donor.components.EmergencyListUI
import com.social.vitadrop.presentation.screens.donor.components.StatsSectionUI
import com.social.vitadrop.presentation.viewmodel.DonorDashboardViewModel

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.social.vitadrop.presentation.event.EmergencyEvent
import com.social.vitadrop.presentation.screens.common.EmergencyListScreen
import com.social.vitadrop.presentation.viewmodel.EmergencyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonorDashboardScreen(
    navController: NavController,
    viewModel: DonorDashboardViewModel,
    emergencyViewModel: EmergencyViewModel
) {

    val state by viewModel.state.collectAsState()
    //val emergencyViewModel: EmergencyViewModel =
       // viewModel()

    //val context = LocalContext.current
    //===============

    val primaryRed = Color(0xFFD50000)

    LaunchedEffect(Unit) {
        Log.d("Dashboard", "LoadDashboard called")
        viewModel.onEvent(
            DonorDashboardEvent.LoadDashboard
        )
    }

    Scaffold(

        containerColor = Color(0xFFF43B3B),

        // ================= TOP BAR =================

        topBar = {

            Surface(
                shadowElevation = 4.dp,
                color = Color.White
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFF01414),
                                    Color(0xFFCE2A2A)
                                )
                            )
                        )
                        .padding(
                            start = 18.dp,
                            end = 18.dp,
                            top = 50.dp,
                            bottom = 16.dp
                        ),

                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    // LEFT SECTION
                    Column {

                        Text(
                            text = "Good Morning 👋",
                            color = Color.White,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "VitaDrop",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // PROFILE BUTTON
                    Card(
                        modifier = Modifier.size(48.dp),

                        shape = CircleShape,

                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFD1DBD9)
                        )
                    ) {

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {

                            IconButton(
                                onClick = {
                                    navController.navigate("profile")
                                }
                            ) {

                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF00D5CA),
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                }
            }
        },

        // ================= BOTTOM BAR =================

        bottomBar = {

            NavigationBar(
                containerColor = Color.White
            ) {

                NavigationBarItem(
                    selected = true,
                    onClick = { },

                    icon = {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = null
                        )
                    },

                    label = {
                        Text("Home")
                    }
                )

                NavigationBarItem(
                    selected = false,

                    onClick = {
                        navController.navigate("donors_list")
                    },

                    icon = {
                        Icon(
                            Icons.Default.People,
                            contentDescription = null
                        )
                    },

                    label = {
                        Text("Donors")
                    }
                )

                NavigationBarItem(
                    selected = false,

                    onClick = {
                        navController.navigate("request_list")
                    },

                    icon = {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null
                        )
                    },

                    label = {
                        Text("Emergency")
                    }
                )

                NavigationBarItem(
                    selected = false,

                    onClick = {
                        navController.navigate("requestBlood")
                    },

                    icon = {
                        Icon(
                            Icons.Default.Bloodtype,
                            contentDescription = null
                        )
                    },

                    label = {
                        Text("Request")
                    }
                )
            }
        },

        // ================= AI FAB =================

        floatingActionButton = {

            val infiniteTransition =
                rememberInfiniteTransition(
                    label = "ai_animation"
                )

            val scale by infiniteTransition.animateFloat(

                initialValue = 1f,

                targetValue = 1.05f,

                animationSpec = infiniteRepeatable(

                    animation = tween(1500),

                    repeatMode = RepeatMode.Reverse
                ),

                label = "scale"
            )

            FloatingActionButton(

                onClick = {
                    navController.navigate("chat_assistant")
                },

                modifier = Modifier
                    .scale(scale)
                    .size(58.dp),

                shape = CircleShape,

                containerColor = Color(0xFF52D2E6),

                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 8.dp
                )
            ) {

                Icon(
                    imageVector = Icons.Outlined.Adb,
                    contentDescription = null,

                    tint = Color.White,

                    modifier = Modifier.size(28.dp)
                )
            }
        }

    ) { padding ->

        LazyColumn(

            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F8F8))
                .padding(padding)
                .padding(horizontal = 18.dp),

            verticalArrangement =
                Arrangement.spacedBy(18.dp)
        ) {

            item {

                Spacer(modifier = Modifier.height(14.dp))
            }

            // ================= STATS =================

            item {

                StatsSectionUI(
                    state.donorsCount,
                    state.hospitalsCount,
                    state.requestsCount
                )
            }

            // ================= EMERGENCY =================

            item {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween,
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(
                        text = "Emergency Requests",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    TextButton(
                        onClick = {
                            navController.navigate(
                                "request_list"
                            )
                        }
                    ) {

                        Text("See All")
                    }
                }
            }

            item {

                EmergencyListScreen(
                    requests =
                        state.emergencyRequests,
                    emergencyViewModel =
                        emergencyViewModel,
                    onViewContact = { requestId ->

                        navController.navigate(
                            "request_details/$requestId"
                        )
                    }
                )
            }
        }
    }
}


