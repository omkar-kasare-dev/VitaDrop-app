package com.social.vitadrop.presentation.profile


import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.social.vitadrop.domain.model.UserProfile
import com.social.vitadrop.domain.model.UserRole
import com.social.vitadrop.presentation.components.VitaDropLoader
import com.social.vitadrop.presentation.components.VitaDropLoaderStyle

// Modern Real-World Design Palette (unchanged)
private val AppBgColor = Color(0xFFF8FAFC)
private val CardBgColor = Color(0xFFFFFFFF)
private val BorderColor = Color(0xFFF1F5F9)
private val TextPrimary = Color(0xFF0F172A)
private val TextSecondary = Color(0xFF64748B)
private val PrimaryAccent = Color(0xFFE11D48)
private val LightAccentBg = Color(0xFFFFE4E6)
private val SoftBadgeBg = Color(0xFFFFF1F2)

// =====================================================================
// STATEFUL WRAPPER
// =====================================================================
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onBack: () -> Unit,
    onLoggedOut: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val currentOnLoggedOut by rememberUpdatedState(onLoggedOut)

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ProfileEffect.LoggedOut -> currentOnLoggedOut()
            }
        }
    }

    ProfileContent(
        state = state,
        onIntent = viewModel::onIntent,
        onBack = onBack
    )
}

// =====================================================================
// STATELESS UI
// =====================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileContent(
    state: ProfileState,
    onIntent: (ProfileIntent) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // Pure UI state: is the "Log out?" dialog open?
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = AppBgColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Profile",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CardBgColor)
            )
        }
    ) { padding ->

        val profile = state.profile

        when {
            // LOADED
            profile != null -> {
                ProfileBody(
                    profile = profile,
                    isLoggingOut = state.isLoggingOut,
                    padding = padding,
                    onComingSoon = {
                        Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show()
                    },
                    onLogoutClick = { showLogoutDialog = true }
                )
            }

            // LOADING
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    VitaDropLoader(
                        style = VitaDropLoaderStyle.Dialog,
                        message = "Loading...."
                    )
                }
            }

            // ERROR + RETRY (before: an empty profile full of "N/A" was shown)
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = state.errorMessage ?: "Could not load your profile",
                        color = Color.Red
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { onIntent(ProfileIntent.Refresh) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent)
                    ) {
                        Text("Retry")
                    }

                    // The user must never be stuck on a broken profile
                    TextButton(onClick = { showLogoutDialog = true }) {
                        Text("Log out", color = PrimaryAccent)
                    }
                }
            }
        }
    }

    // LOGOUT CONFIRMATION (before: one accidental tap logged the user out)
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Log out?") },
            text = { Text("You will stop receiving blood request notifications on this device.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onIntent(ProfileIntent.Logout)
                    }
                ) {
                    Text("Log out", color = PrimaryAccent)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ProfileBody(
    profile: UserProfile,
    isLoggingOut: Boolean,
    padding: PaddingValues,
    onComingSoon: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        // PROFILE HEADER CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBgColor),
            border = BorderStroke(1.dp, BorderColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(SoftBadgeBg)
                        .border(1.dp, LightAccentBg, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = PrimaryAccent,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = profile.fullName.ifEmpty { "N/A" },
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = profile.email.ifEmpty { "N/A" },
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // ROLE BADGE
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = SoftBadgeBg,
                    border = BorderStroke(1.dp, LightAccentBg)
                ) {
                    Text(
                        text = profile.role.name,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                        color = PrimaryAccent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        // BASIC INFORMATION
        ProfileSectionCard(title = "BASIC INFORMATION") {
            ProfileInfoRow("Phone", profile.phone)
            ProfileInfoRow("City", profile.city)
            ProfileInfoRow("State", profile.state)
            ProfileInfoRow("Address", profile.address, isLast = true)
        }

        // ROLE SPECIFIC DETAILS
        when (profile.role) {
            UserRole.DONOR -> {
                ProfileSectionCard(title = "DONOR DETAILS") {
                    ProfileInfoRow("Gender", profile.gender)
                    ProfileInfoRow("Age", if (profile.age > 0) profile.age.toString() else "")
                    ProfileInfoRow("Blood Group", profile.bloodGroup)
                    ProfileInfoRow(
                        "Weight",
                        if (profile.weightKg > 0) "${profile.weightKg} kg" else ""
                    )
                    ProfileInfoRow("Available", if (profile.isAvailable) "Yes" else "No")
                    ProfileInfoRow(
                        "Verification",
                        if (profile.isVerified) "Verified" else "Pending",
                        isLast = true
                    )
                }
            }

            UserRole.HOSPITAL -> {
                ProfileSectionCard(title = "HOSPITAL DETAILS") {
                    ProfileInfoRow("License Number", profile.licenseNumber)
                    ProfileInfoRow(
                        "Verification",
                        if (profile.isVerified) "Verified" else "Pending",
                        isLast = true
                    )
                }
            }
        }

        // ACCOUNT SETTINGS
        Text(
            text = "ACCOUNT SETTINGS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            letterSpacing = 0.8.sp,
            modifier = Modifier.padding(start = 4.dp, top = 6.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBgColor),
            border = BorderStroke(1.dp, BorderColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column {
                // These three rows had an empty click handler before. They now say so.
                ProfileOptionRow(
                    title = "Edit Profile",
                    icon = Icons.Default.Edit,
                    onClick = onComingSoon
                )
                HorizontalDivider(color = BorderColor)

                ProfileOptionRow(
                    title = "My Blood Requests",
                    icon = Icons.Default.Bloodtype,
                    onClick = onComingSoon
                )
                HorizontalDivider(color = BorderColor)

                ProfileOptionRow(
                    title = "Settings",
                    icon = Icons.Default.Settings,
                    onClick = onComingSoon
                )
                HorizontalDivider(color = BorderColor)

                ProfileOptionRow(
                    title = if (isLoggingOut) "Logging out..." else "Logout",
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    textColor = PrimaryAccent,
                    onClick = { if (!isLoggingOut) onLogoutClick() }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun ProfileSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBgColor),
        border = BorderStroke(1.dp, BorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            content()
        }
    }
}

@Composable
private fun ProfileInfoRow(
    label: String,
    value: String,
    isLast: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = value.ifEmpty { "N/A" },
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
        }

        if (!isLast) {
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = BorderColor, thickness = 1.dp)
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun ProfileOptionRow(
    title: String,
    icon: ImageVector,
    textColor: Color = TextPrimary,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(SoftBadgeBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = PrimaryAccent,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = textColor,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}