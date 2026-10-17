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
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Diversity2
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Warning
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
// 1. DONOR DASHBOARD DESIGN (Minimal Rose / Light Red Theme)
// =====================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonorDashboardContent(
    state: DonorDashboardState,
    emergencyViewModel: EmergencyViewModel,
    onRefresh: () -> Unit,
    onProfileClick: () -> Unit,
    onEmergencyClick: () -> Unit,
    onChatClick: () -> Unit,
    onViewContact: (String) -> Unit
) {
    val bgCanvas = Color(0xFFF8FAFC)
    val surfaceCard = Color(0xFFFFFFFF)
    val primaryRose = Color(0xFFE11D48)
    val softRoseBg = Color(0xFFFFF1F2)
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
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(softRoseBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Diversity2,
                                contentDescription = null,
                                tint = primaryRose,
                                modifier = Modifier.size(35.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "VitaDrop",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = textDark,
                                letterSpacing = (-0.3).sp
                            )
                            Text(
                                text = "Donor Portal",
                                fontSize = 10.sp,
                                color = primaryRose,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(softRoseBg)
                            .border(0.8.dp, primaryRose.copy(alpha = 0.2f), CircleShape)
                            .clickable { onProfileClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = primaryRose,
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
                    icon = { Icon(Icons.Default.Home, contentDescription = null, tint = primaryRose, modifier = Modifier.size(20.dp)) },
                    label = { Text("Home", color = primaryRose, fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = softRoseBg)
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
                    colors = CardDefaults.cardColors(containerColor = primaryRose)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Ready to Save a Life?",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Check emergency blood requests around your area and respond to nearby hospitals instantly.",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
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
                    DashboardErrorBanner(message = message, onRetry = onRefresh, accentColor = primaryRose)
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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Emergency Requests",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = textDark
                        )
                        Text(
                            text = "Urgent blood requests needing donors",
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
                            color = primaryRose,
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
                    EmergencyListScreen(
                        requests = state.emergencyRequests,
                        emergencyViewModel = emergencyViewModel,
                        role = UserRole.DONOR,
                        onViewContact = onViewContact
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }
        }
    }
}

// =====================================================================
// FUTURISTIC BLOOD NETWORK AI FLOATING ACTION BUTTON
// =====================================================================

