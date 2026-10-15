package com.social.vitadrop.presentation.dashboard.screen
/*
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
import com.social.vitadrop.domain.model.UserRole
import com.social.vitadrop.presentation.emergency.screen.EmergencyListScreen
import com.social.vitadrop.presentation.emergency.viewmodel.EmergencyViewModel
// StatsSectionUI is not migrated yet (I have not seen it), so its import stays as it was.
import com.social.vitadrop.presentation.components.StatsSectionUI
import com.social.vitadrop.presentation.components.VitaDropLoader
import com.social.vitadrop.presentation.components.VitaDropLoaderStyle
import com.social.vitadrop.presentation.dashboard.DonorDashboardIntent
import com.social.vitadrop.presentation.dashboard.DonorDashboardState
import com.social.vitadrop.presentation.dashboard.viewmodel.DonorDashboardViewModel

// =====================================================================
// STATEFUL WRAPPER: ViewModel + refresh when the screen opens
// =====================================================================
@Composable
fun DonorDashboardScreen(
    role: UserRole,
    viewModel: DonorDashboardViewModel,
    emergencyViewModel: EmergencyViewModel,
    onProfileClick: () -> Unit,
    onDonorsClick: () -> Unit,
    onEmergencyClick: () -> Unit,
    onRequestBloodClick: () -> Unit,
    onChatClick: () -> Unit,
    onViewContact: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()

    // Runs every time the dashboard enters the screen (first open AND coming back from another
    // screen), so counts are fresh after e.g. a hospital created a request.
    LaunchedEffect(Unit) {
        viewModel.onIntent(DonorDashboardIntent.Refresh)
    }

    DashboardContent(
        state = state,
        role = role,
        emergencyViewModel = emergencyViewModel,
        onRefresh = { viewModel.onIntent(DonorDashboardIntent.Refresh) },
        onProfileClick = onProfileClick,
        onDonorsClick = onDonorsClick,
        onEmergencyClick = onEmergencyClick,
        onRequestBloodClick = onRequestBloodClick,
        onChatClick = onChatClick,
        onViewContact = onViewContact
    )
}

// =====================================================================
// STATELESS UI
// =====================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardContent(
    state: DonorDashboardState,
    role: UserRole,
    emergencyViewModel: EmergencyViewModel,
    onRefresh: () -> Unit,
    onProfileClick: () -> Unit,
    onDonorsClick: () -> Unit,
    onEmergencyClick: () -> Unit,
    onRequestBloodClick: () -> Unit,
    onChatClick: () -> Unit,
    onViewContact: (String) -> Unit
) {
    // Modern Production Palette
    val bgCanvas = Color(0xFFF8FAFC)
    val surfaceCard = Color(0xFFFFFFFF)
    val primaryRose = Color(0xFFE11D48)
    val softRoseBg = Color(0xFFFFF1F2)
    val borderLight = Color(0xFFE2E8F0)
    val freshEmerald = Color(0xFF10B981)
    val textDark = Color(0xFF0F172A)
    val textMuted = Color(0xFF64748B)

    // Only hospitals manage donors and create requests
    val isHospital = role == UserRole.HOSPITAL

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
                            .clickable { onProfileClick() },
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

                // Donor list shows phone numbers: hospitals only
                if (isHospital) {
                    NavigationBarItem(
                        selected = false,
                        onClick = onDonorsClick,
                        icon = { Icon(Icons.Default.People, contentDescription = null, tint = textMuted, modifier = Modifier.size(20.dp)) },
                        label = { Text("Donors", color = textMuted, fontSize = 10.sp) }
                    )
                }

                NavigationBarItem(
                    selected = false,
                    onClick = onEmergencyClick,
                    icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = textMuted, modifier = Modifier.size(20.dp)) },
                    label = { Text("Emergency", color = textMuted, fontSize = 10.sp) }
                )

                // Only hospitals can create blood requests
                if (isHospital) {
                    NavigationBarItem(
                        selected = false,
                        onClick = onRequestBloodClick,
                        icon = { Icon(Icons.Default.Bloodtype, contentDescription = null, tint = textMuted, modifier = Modifier.size(20.dp)) },
                        label = { Text("Request", color = textMuted, fontSize = 10.sp) }
                    )
                }
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
                onClick = onChatClick,
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

            // LOADING BAR (before: the screen never showed loading)
            if (state.isLoading) {
                item {
                    VitaDropLoader(
                        style = VitaDropLoaderStyle.Dialog,
                        message = "Loading...."
                    )
                }
            }

            // ERROR + RETRY (before: failures were hidden and zeros were shown)
            state.error?.let { message ->
                item {
                    DashboardErrorBanner(message = message, onRetry = onRefresh)
                }
            }

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

            // EMERGENCY LIST (or an empty message)
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
                        onViewContact = onViewContact
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }
        }
    }
}

@Composable
private fun DashboardErrorBanner(
    message: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = message,
                color = Color(0xFFC62828),
                fontSize = 12.sp,
                modifier = Modifier.weight(1f)
            )

            TextButton(onClick = onRetry) {
                Text(text = "Retry", color = Color(0xFFE11D48))
            }
        }
    }
}

 */

import androidx.compose.runtime.*
import com.social.vitadrop.domain.model.UserRole
import com.social.vitadrop.presentation.emergency.viewmodel.EmergencyViewModel
import com.social.vitadrop.presentation.dashboard.DonorDashboardIntent
import com.social.vitadrop.presentation.dashboard.components.DonorDashboardContent
import com.social.vitadrop.presentation.dashboard.components.HospitalDashboardContent
import com.social.vitadrop.presentation.dashboard.viewmodel.DonorDashboardViewModel

@Composable
fun DonorDashboardScreen(
    role: UserRole,
    viewModel: DonorDashboardViewModel,
    emergencyViewModel: EmergencyViewModel,
    onProfileClick: () -> Unit,
    onDonorsClick: () -> Unit,
    onEmergencyClick: () -> Unit,
    onRequestBloodClick: () -> Unit,
    onChatClick: () -> Unit,
    onViewContact: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onIntent(DonorDashboardIntent.Refresh)
    }

    if (role == UserRole.HOSPITAL) {
        HospitalDashboardContent(
            state = state,
            emergencyViewModel = emergencyViewModel,
            onRefresh = { viewModel.onIntent(DonorDashboardIntent.Refresh) },
            onProfileClick = onProfileClick,
            onDonorsClick = onDonorsClick,
            onEmergencyClick = onEmergencyClick,
            onRequestBloodClick = onRequestBloodClick,
            onChatClick = onChatClick,
            onViewContact = onViewContact
        )
    } else {
        DonorDashboardContent(
            state = state,
            emergencyViewModel = emergencyViewModel,
            onRefresh = { viewModel.onIntent(DonorDashboardIntent.Refresh) },
            onProfileClick = onProfileClick,
            onEmergencyClick = onEmergencyClick,
            onChatClick = onChatClick,
            onViewContact = onViewContact
        )
    }
}

