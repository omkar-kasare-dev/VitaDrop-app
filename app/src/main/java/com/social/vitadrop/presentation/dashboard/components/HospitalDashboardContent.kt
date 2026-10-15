package com.social.vitadrop.presentation.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.social.vitadrop.domain.model.UserRole
import com.social.vitadrop.presentation.components.StatsSectionUI
import com.social.vitadrop.presentation.components.VitaDropLoader
import com.social.vitadrop.presentation.components.VitaDropLoaderStyle
import com.social.vitadrop.presentation.dashboard.DonorDashboardState
import com.social.vitadrop.presentation.emergency.screen.EmergencyListScreen
import com.social.vitadrop.presentation.emergency.viewmodel.EmergencyViewModel

// =====================================================================
// 2. HOSPITAL DASHBOARD DESIGN (Minimal Sky Blue Theme)
// =====================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HospitalDashboardContent(
    state: DonorDashboardState,
    emergencyViewModel: EmergencyViewModel,
    onRefresh: () -> Unit,
    onProfileClick: () -> Unit,
    onDonorsClick: () -> Unit,
    onEmergencyClick: () -> Unit,
    onRequestBloodClick: () -> Unit,
    onChatClick: () -> Unit,
    onViewContact: (String) -> Unit
) {
    val bgCanvas = Color(0xFFF8FAFC)
    val surfaceCard = Color(0xFFFFFFFF)
    val primaryBlue = Color(0xFF0284C7)
    val softBlueBg = Color(0xFFE0F2FE)
    val borderLight = Color(0xFFE2E8F0)
    val textDark = Color(0xFF0F172A)
    val textMuted = Color(0xFF64748B)

    Scaffold(
        containerColor = bgCanvas,
        topBar = {
            Surface(
                color = surfaceCard,
                modifier = Modifier.border(width = 0.5.dp, color = borderLight)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(softBlueBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalHospital,
                                contentDescription = null,
                                tint = primaryBlue,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "VitaDrop",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = textDark,
                                letterSpacing = (-0.3).sp
                            )
                            Text(
                                text = "Hospital Console",
                                fontSize = 10.sp,
                                color = primaryBlue,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(softBlueBg)
                            .border(0.8.dp, primaryBlue.copy(alpha = 0.2f), CircleShape)
                            .clickable { onProfileClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = primaryBlue,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = surfaceCard,
                tonalElevation = 0.dp,
                modifier = Modifier.border(width = 0.5.dp, color = borderLight)
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Home, contentDescription = null, tint = primaryBlue, modifier = Modifier.size(20.dp)) },
                    label = { Text("Home", color = primaryBlue, fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = softBlueBg)
                )

                NavigationBarItem(
                    selected = false,
                    onClick = onDonorsClick,
                    icon = { Icon(Icons.Default.People, contentDescription = null, tint = textMuted, modifier = Modifier.size(20.dp)) },
                    label = { Text("Donors", color = textMuted, fontSize = 10.sp) }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = onEmergencyClick,
                    icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = textMuted, modifier = Modifier.size(20.dp)) },
                    label = { Text("Emergency", color = textMuted, fontSize = 10.sp) }
                )

            }
        },
        floatingActionButton = {
            FuturisticBloodNetworkAIFab(onClick = onChatClick)
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(bgCanvas)
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = primaryBlue)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Manage Blood Requirements",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Create new urgent emergency requests or search registered blood donors directly.",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onRequestBloodClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = primaryBlue),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Create Request", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (state.isLoading) {
                item {
                    VitaDropLoader(
                        style = VitaDropLoaderStyle.Dialog,
                        message = "Loading...."
                    )
                }
            }

            state.error?.let { message ->
                item {
                    DashboardErrorBanner(message = message, onRetry = onRefresh, accentColor = primaryBlue)
                }
            }

            item {
                StatsSectionUI(
                    state.donorsCount,
                    state.hospitalsCount,
                    state.requestsCount
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionButton(
                        modifier = Modifier.weight(1f),
                        title = "Browse Donors",
                        icon = Icons.Default.People,
                        bgColor = softBlueBg,
                        tintColor = primaryBlue,
                        onClick = onDonorsClick
                    )
                    QuickActionButton(
                        modifier = Modifier.weight(1f),
                        title = "Emergency Hub",
                        icon = Icons.Default.Warning,
                        bgColor = softBlueBg,
                        tintColor = primaryBlue,
                        onClick = onEmergencyClick
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Active Emergency Requests",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = textDark
                        )
                        Text(
                            text = "Live blood requests across network",
                            fontSize = 11.sp,
                            color = textMuted
                        )
                    }

                    TextButton(
                        onClick = onEmergencyClick,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                    ) {
                        Text(
                            text = "See All",
                            color = primaryBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            item {
                if (!state.isLoading && state.error == null && state.emergencyRequests.isEmpty()) {
                    Text(
                        text = "No emergency requests right now.",
                        fontSize = 12.sp,
                        color = textMuted
                    )
                } else {
                    // Explicitly passing UserRole.HOSPITAL down to EmergencyListScreen
                    EmergencyListScreen(
                        requests = state.emergencyRequests,
                        emergencyViewModel = emergencyViewModel,
                        role = UserRole.HOSPITAL,
                        onViewContact = onViewContact
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }
        }
    }
}