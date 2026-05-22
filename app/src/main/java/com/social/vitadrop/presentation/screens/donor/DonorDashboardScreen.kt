package com.social.vitadrop.presentation.screens.donor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.social.vitadrop.presentation.event.DonorDashboardEvent
import com.social.vitadrop.presentation.screens.donor.components.EmergencyListUI
import com.social.vitadrop.presentation.screens.donor.components.StatsSectionUI
import com.social.vitadrop.presentation.viewmodel.DonorDashboardViewModel

// AI Assistant Imports
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material3.*
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonorDashboardScreen(
    navController: NavController,
    viewModel: DonorDashboardViewModel
) {

    val state by viewModel.state.collectAsState()


    LaunchedEffect(Unit) {
        viewModel.onEvent(DonorDashboardEvent.LoadDashboard)

    }

    Scaffold(

        // TOP BAR
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Icon(
                            imageVector = Icons.Default.Bloodtype,
                            contentDescription = null,
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(32.dp)
                        )

                        Spacer(modifier = Modifier.width(10.dp))
                        Text("VitaDrop", fontSize = 25.sp)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("profile")
                    }) {
                        Icon(Icons.Default.AccountCircle, null)
                    }
                }
            )
        },

        // BOTTOM BAR
        bottomBar = {
            NavigationBar {

                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Home") }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("donors_list") },
                    icon = { Icon(Icons.Default.People, null) },
                    label = { Text("Donors") }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("request_list") },
                    icon = { Icon(Icons.Default.AddAlert, null) },
                    label = { Text("List") }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("requestBlood") },
                    icon = { Icon(Icons.Default.Bloodtype, null) },
                    label = { Text("Request Blood") }
                )
            }
        },

        // FAB
        floatingActionButton = {

            // Soft Breathing Animation
            val infiniteTransition =
                rememberInfiniteTransition(
                    label = "ai_button_anim"
                )

            val scale by infiniteTransition.animateFloat(

                initialValue = 1f,

                targetValue = 1.04f,

                animationSpec = infiniteRepeatable(

                    animation = tween(
                        durationMillis = 1400
                    ),

                    repeatMode = RepeatMode.Reverse
                ),

                label = "scale_animation"
            )

            Row(

                modifier = Modifier
                    .scale(scale)
                    .padding(end = 6.dp),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                // ============================================
                // TEXT SECTION
                // ============================================

                Card(

                    onClick = {

                        navController.navigate(
                            "chat_assistant"
                        )
                    },

                    shape = RoundedCornerShape(18.dp),

                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1E293B)
                    ),

                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 8.dp
                    )
                ) {

                    Row(

                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 12.dp
                        ),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Column {

                            Text(

                                text = "Hemora AI",

                                color = Color.White,

                                style =
                                    MaterialTheme.typography.titleSmall,

                                fontWeight = FontWeight.Bold
                            )


                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // ============================================
                // AI SYMBOL SECTION
                // ============================================

                FloatingActionButton(

                    onClick = {

                        navController.navigate(
                            "chat_assistant"
                        )
                    },

                    modifier = Modifier.size(58.dp),

                    shape = CircleShape,

                    containerColor = Color.Transparent,

                    elevation =
                        FloatingActionButtonDefaults.elevation(
                            defaultElevation = 10.dp
                        )
                ) {

                    Box(

                        modifier = Modifier
                            .fillMaxSize()

                            .background(

                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF06B6D4),
                                        Color(0xFF3B82F6)
                                    )
                                ),

                                shape = CircleShape
                            ),

                        contentAlignment = Alignment.Center
                    ) {

                        Icon(

                            imageVector =
                                Icons.Outlined.Psychology,

                            contentDescription = null,

                            tint = Color.White,

                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }

        // AI Assistant:



    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFF5F5))
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                ) //keep side & bottom padding only
                .padding(
                    top = padding.calculateTopPadding() + 8.dp,
                    bottom = padding.calculateBottomPadding()
                ), // correct scaffold spacing
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            //item { BannerImage(R.drawable.blood_banner) }

            item {
                StatsSectionUI(
                    state.donorsCount,
                    state.hospitalsCount,
                    state.requestsCount
                )
            }

            item {
                EmergencyListUI(state.emergencyRequests)
            }

            // item { BannerImage(R.drawable.blood_banner2) }

            item {
                Text("Quick Actions")
            }

            item {
                //QuickActionGrid(navController)
            }
        }
    }
}