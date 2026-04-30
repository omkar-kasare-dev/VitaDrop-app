package com.social.vitadrop.presentation.screens.donor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import com.social.vitadrop.presentation.event.RequestEvent
import com.social.vitadrop.presentation.screens.donor.components.EmergencyListUI
import com.social.vitadrop.presentation.screens.donor.components.StatsSectionUI
import com.social.vitadrop.presentation.viewmodel.DonorDashboardViewModel

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

        // ✅ TOP BAR
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

        // ✅ BOTTOM BAR
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
                    icon = { Icon(Icons.Default.Bloodtype, null) },
                    label = { Text("Request") }
                )
            }
        },

        // ✅ FAB
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    //navController.navigate("addDonor")
                    navController.navigate("requestBlood")
                },
                containerColor = Color(0xFFD32F2F)
            ) {
                Icon(Icons.Default.Add, null)
            }
        }

    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFF5F5))
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                ) // ✅ keep side & bottom padding only
                .padding(
                    top = padding.calculateTopPadding() + 8.dp,
                    bottom = padding.calculateBottomPadding()
                ), // ✅ correct scaffold spacing
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