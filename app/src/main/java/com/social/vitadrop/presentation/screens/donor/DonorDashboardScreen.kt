package com.social.vitadrop.presentation.screens.donor

/* Dark Pallete
import android.content.Intent
import android.net.Uri
import android.util.Log
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

import com.social.vitadrop.presentation.event.DonorDashboardEvent
import com.social.vitadrop.presentation.event.EmergencyEvent
import com.social.vitadrop.presentation.screens.common.EmergencyListScreen
import com.social.vitadrop.presentation.screens.donor.components.EmergencyListUI
import com.social.vitadrop.presentation.screens.donor.components.StatsSectionUI
import com.social.vitadrop.presentation.viewmodel.DonorDashboardViewModel
import com.social.vitadrop.presentation.viewmodel.EmergencyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonorDashboardScreen(
    navController: NavController,
    viewModel: DonorDashboardViewModel,
    emergencyViewModel: EmergencyViewModel
) {

    val state by viewModel.state.collectAsState()

    // 2030 Futuristic Palette
    val bgDark = Color(0xFF090A0F)
    val bgSurface = Color(0xFF12141D)
    val cyberRed = Color(0xFFFF0033)
    val neonCrimson = Color(0xFFFF2A55)
    val glassBorder = Color(0xFF2A2E3D)
    val textPrimary = Color(0xFFF0F2F8)
    val textMuted = Color(0xFF7E849B)

    LaunchedEffect(Unit) {
        Log.d("Dashboard", "LoadDashboard called")
        viewModel.onEvent(
            DonorDashboardEvent.LoadDashboard
        )
    }

    Scaffold(
        containerColor = bgDark,

        // ================= TOP BAR =================
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF160307),
                                bgDark
                            )
                        )
                    )
                    .border(
                        width = 0.8.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(cyberRed.copy(alpha = 0.3f), Color.Transparent)
                        ),
                        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                    )
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = 48.dp,
                        bottom = 18.dp
                    )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // LEFT SECTION
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(cyberRed)
                            )
                            Text(
                                text = "BIO-LINK ONLINE",
                                color = cyberRed,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "VitaDrop",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = textPrimary,
                            letterSpacing = (-0.5).sp
                        )
                    }

                    // PROFILE BUTTON
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(bgSurface)
                            .border(1.dp, cyberRed.copy(alpha = 0.4f), CircleShape)
                            .clickable {
                                navController.navigate("profile")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = neonCrimson,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        },

        // ================= BOTTOM BAR =================
        bottomBar = {
            NavigationBar(
                containerColor = bgSurface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .border(
                        width = 0.8.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(glassBorder, Color.Transparent)
                        ),
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    )
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = "Home",
                            tint = cyberRed
                        )
                    },
                    label = {
                        Text(
                            "Home",
                            color = cyberRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = cyberRed.copy(alpha = 0.15f)
                    )
                )

                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate("donors_list")
                    },
                    icon = {
                        Icon(
                            Icons.Default.People,
                            contentDescription = "Donors",
                            tint = textMuted
                        )
                    },
                    label = {
                        Text(
                            "Donors",
                            color = textMuted,
                            fontSize = 11.sp
                        )
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
                            contentDescription = "Emergency",
                            tint = textMuted
                        )
                    },
                    label = {
                        Text(
                            "Emergency",
                            color = textMuted,
                            fontSize = 11.sp
                        )
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
                            contentDescription = "Request",
                            tint = textMuted
                        )
                    },
                    label = {
                        Text(
                            "Request",
                            color = textMuted,
                            fontSize = 11.sp
                        )
                    }
                )
            }
        },

        // ================= AI FAB =================
        floatingActionButton = {
            val infiniteTransition = rememberInfiniteTransition(label = "ai_animation")

            val scale by infiniteTransition.animateFloat(
                initialValue = 0.95f,
                targetValue = 1.06f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1500, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "scale"
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .scale(scale)
                    .size(62.dp)
            ) {
                // Outer Pulse Halo
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFF00E5FF).copy(alpha = 0.3f), Color.Transparent)
                            )
                        )
                )

                FloatingActionButton(
                    onClick = {
                        navController.navigate("chat_assistant")
                    },
                    modifier = Modifier.size(54.dp),
                    shape = CircleShape,
                    containerColor = Color(0xFF00E5FF),
                    contentColor = bgDark,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 10.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Adb,
                        contentDescription = "AI Assistant",
                        tint = bgDark,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgDark)
                .padding(padding)
        ) {
            // Ambient Top Plasma Radial Lighting
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .align(Alignment.TopCenter)
                    .offset(y = (-100).dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(cyberRed.copy(alpha = 0.18f), Color.Transparent)
                        ),
                        shape = CircleShape
                    )
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // ================= STATS =================
                item {
                    StatsSectionUI(
                        state.donorsCount,
                        state.hospitalsCount,
                        state.requestsCount
                    )
                }

                // ================= EMERGENCY HEADER =================
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "CRITICAL EMERGENCY REQUESTS",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = textPrimary,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Real-time bio-net alerts needing immediate match",
                                fontSize = 11.sp,
                                color = textMuted
                            )
                        }

                        TextButton(
                            onClick = {
                                navController.navigate("request_list")
                            }
                        ) {
                            Text(
                                text = "VIEW ALL",
                                color = neonCrimson,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                // ================= EMERGENCY LIST =================
                item {
                    EmergencyListScreen(
                        requests = state.emergencyRequests,
                        emergencyViewModel = emergencyViewModel,
                        onViewContact = { requestId ->
                            navController.navigate("request_details/$requestId")
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}
 */



import android.util.Log
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.social.vitadrop.presentation.event.DonorDashboardEvent
import com.social.vitadrop.presentation.screens.common.EmergencyListScreen
import com.social.vitadrop.presentation.screens.donor.components.StatsSectionUI
import com.social.vitadrop.presentation.viewmodel.DonorDashboardViewModel
import com.social.vitadrop.presentation.viewmodel.EmergencyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonorDashboardScreen(
    navController: NavController,
    viewModel: DonorDashboardViewModel,
    emergencyViewModel: EmergencyViewModel
) {
    val state by viewModel.state.collectAsState()

    // Modern Production Palette
    val bgCanvas = Color(0xFFF8FAFC)
    val surfaceCard = Color(0xFFFFFFFF)
    val primaryRose = Color(0xFFE11D48)
    val softRoseBg = Color(0xFFFFF1F2)
    val borderLight = Color(0xFFE2E8F0)
    val freshEmerald = Color(0xFF10B981)
    val textDark = Color(0xFF0F172A)
    val textMuted = Color(0xFF64748B)

    LaunchedEffect(Unit) {
        Log.d("Dashboard", "LoadDashboard called")
        viewModel.onEvent(DonorDashboardEvent.LoadDashboard)
    }

    Scaffold(
        containerColor = bgCanvas,

        // ================= TOP BAR =================
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
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(freshEmerald)
                            )

                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "VitaDrop",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = textDark,
                            letterSpacing = (-0.3).sp
                        )
                    }

                    // PROFILE BUTTON
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(softRoseBg)
                            .border(0.8.dp, primaryRose.copy(alpha = 0.2f), CircleShape)
                            .clickable { navController.navigate("profile") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = primaryRose,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        },

        // ================= BOTTOM BAR =================
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
                    onClick = { navController.navigate("donors_list") },
                    icon = { Icon(Icons.Default.People, contentDescription = null, tint = textMuted, modifier = Modifier.size(20.dp)) },
                    label = { Text("Donors", color = textMuted, fontSize = 10.sp) }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("request_list") },
                    icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = textMuted, modifier = Modifier.size(20.dp)) },
                    label = { Text("Emergency", color = textMuted, fontSize = 10.sp) }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("requestBlood") },
                    icon = { Icon(Icons.Default.Bloodtype, contentDescription = null, tint = textMuted, modifier = Modifier.size(20.dp)) },
                    label = { Text("Request", color = textMuted, fontSize = 10.sp) }
                )
            }
        },

        // ================= AI FAB =================
        floatingActionButton = {
            val infiniteTransition = rememberInfiniteTransition(label = "ai_animation")
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.97f,
                targetValue = 1.03f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1500, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "scale"
            )

            FloatingActionButton(
                onClick = { navController.navigate("chat_assistant") },
                modifier = Modifier
                    .scale(scale)
                    .size(44.dp),
                shape = CircleShape,
                containerColor = primaryRose,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Adb,
                    contentDescription = "AI Assistant",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
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
            item { Spacer(modifier = Modifier.height(6.dp)) }

            // STATS SECTION
            item {
                StatsSectionUI(
                    state.donorsCount,
                    state.hospitalsCount,
                    state.requestsCount
                )
            }

            // EMERGENCY REQUESTS HEADER
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
                        onClick = { navController.navigate("request_list") },
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

            // EMERGENCY LIST
            item {
                EmergencyListScreen(
                    requests = state.emergencyRequests,
                    emergencyViewModel = emergencyViewModel,
                    onViewContact = { requestId ->
                        navController.navigate("request_details/$requestId")
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }
        }
    }
}