package com.social.vitadrop.presentation.auth.register.screen
/* Dark pallete
import android.Manifest
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.social.vitadrop.domain.model.UserRole
import com.social.vitadrop.presentation.auth.register.components.DonorFields
import com.social.vitadrop.presentation.auth.register.components.HospitalFields

import com.social.vitadrop.utils.LocationHelper
// IMPORTANT: use the R class of your app "namespace" (see app/build.gradle).
// If your namespace is com.social.vitadrop, change this to: import com.social.vitadrop.R
import com.example.kotlinbasics.R
import com.social.vitadrop.presentation.auth.login.components.ModernRoleCard
import com.social.vitadrop.presentation.auth.register.RegisterEffect
import com.social.vitadrop.presentation.auth.register.RegisterField
import com.social.vitadrop.presentation.auth.register.RegisterIntent
import com.social.vitadrop.presentation.auth.register.RegisterState
import com.social.vitadrop.presentation.auth.register.viewmodel.RegisterViewModel
import com.social.vitadrop.presentation.components.VitaDropLoader
import com.social.vitadrop.presentation.components.VitaDropLoaderStyle


// =====================================================================
// STATEFUL WRAPPER: talks to ViewModel, location permission and effects
// =====================================================================
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onNavigateToLogin: () -> Unit,
    onRegistered: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val locationHelper = remember { LocationHelper(context) }

    fun fetchLocation() {
        locationHelper.getCurrentLocation(
            onSuccess = { lat, lng ->
                viewModel.onIntent(RegisterIntent.LocationReceived(lat, lng))
            },
            onFailure = {
                Log.e("REGISTER_GPS", it?.message ?: "Location error")
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) fetchLocation()
    }

    // Ask for / read location once when the screen opens
    LaunchedEffect(Unit) {
        if (locationHelper.hasLocationPermission()) {
            fetchLocation()
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // One-shot effects from the ViewModel (success -> navigate)
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                RegisterEffect.RegistrationSuccess -> {
                    Toast.makeText(
                        context,
                        "Registration successful. Please login.",
                        Toast.LENGTH_LONG
                    ).show()
                    onRegistered()
                }
            }
        }
    }

    RegisterContent(
        state = state,
        onIntent = viewModel::onIntent,
        onNavigateToLogin = onNavigateToLogin
    )
}

// =====================================================================
// STATELESS UI: only draws what the State says, sends Intents back (2030 Futuristic Redesign)
// =====================================================================
@Composable
private fun RegisterContent(
    state: RegisterState,
    onIntent: (RegisterIntent) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    // Futuristic 2030 Cyber-Medical Color Palette
    val bgDark = Color(0xFF090A0F)
    val bgSurface = Color(0xFF12141D)
    val cyberRed = Color(0xFFFF0033)
    val neonCrimson = Color(0xFFFF2A55)
    val glassBorder = Color(0xFF2A2E3D)
    val textPrimary = Color(0xFFF0F2F8)
    val textMuted = Color(0xFF7E849B)

    // Bio-Sync Pulsing Heart Animation
    val infiniteTransition = rememberInfiniteTransition(label = "RegisterBioPulse")
    val heartScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "HeartPulseScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgDark)
    ) {

        // High-Tech Dialog Progress Loader Overlay
        if (state.isLoading) {
            VitaDropLoader(
                style = VitaDropLoaderStyle.Dialog,
                message = "REGISTERING NEURAL ID",
                subMessage = "Registering user credentials..."
            )
        }
        // Ambient Neon Top Radial Glow
        Box(
            modifier = Modifier
                .size(320.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-80).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(cyberRed.copy(alpha = 0.22f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 40.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(90.dp))

            // TITLE
            Text(
                text = "Create Account",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = buildAnnotatedString {
                    append("Join VitaDrop network and ")
                    withStyle(
                        style = SpanStyle(
                            color = neonCrimson,
                            fontWeight = FontWeight.SemiBold
                        )
                    ) {
                        append("save lives today.")
                    }
                },
                fontSize = 12.sp,
                color = textMuted
            )

            Spacer(modifier = Modifier.height(18.dp))

            // ANIMATED BIO-HEART EMBLEM
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(120.dp)
                    .scale(heartScale)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(cyberRed.copy(alpha = 0.2f), Color.Transparent)
                            )
                        )
                )

                Image(
                    painter = painterResource(id = R.drawable.login_heart),
                    contentDescription = null,
                    modifier = Modifier.size(90.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // COMPACT SWITCHER TAB
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(bgSurface)
                    .border(0.8.dp, glassBorder, RoundedCornerShape(12.dp))
                    .padding(3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(9.dp))
                        .clickable { onNavigateToLogin() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Login",
                        color = textMuted,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(9.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(cyberRed, neonCrimson)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Register",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SELECT ROLE SECTION
            Text(
                text = "IDENTITY MATRIX ROLE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = textMuted,
                letterSpacing = 1.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    ModernRoleCard(
                        title = "Donor",
                        icon = "🩸",
                        selected = state.role == UserRole.DONOR
                    ) {
                        onIntent(RegisterIntent.SelectRole(UserRole.DONOR))
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    ModernRoleCard(
                        title = "Hospital",
                        icon = "🏥",
                        selected = state.role == UserRole.HOSPITAL
                    ) {
                        onIntent(RegisterIntent.SelectRole(UserRole.HOSPITAL))
                    }
                }
                // Admin card removed: the data layer does not support an admin registration yet.
            }

            Spacer(modifier = Modifier.height(20.dp))

            // DYNAMIC ROLE FORM
            AnimatedVisibility(
                visible = state.role != null,
                enter = fadeIn() + slideInVertically(initialOffsetY = { 30 }),
                exit = fadeOut()
            ) {
                if (state.role != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(0.8.dp, glassBorder, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = bgSurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            Text(
                                text = "Registering as ${state.role.name}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = cyberRed,
                                letterSpacing = 0.5.sp
                            )

                            // EMAIL FIELD
                            ModernTextField(
                                value = state.email,
                                onValueChange = {
                                    onIntent(RegisterIntent.FieldChanged(RegisterField.EMAIL, it))
                                },
                                label = "Neural Email / ID",
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = null,
                                        tint = cyberRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                keyboardType = KeyboardType.Email
                            )

                            // PASSWORD FIELD
                            ModernTextField(
                                value = state.password,
                                onValueChange = {
                                    onIntent(RegisterIntent.FieldChanged(RegisterField.PASSWORD, it))
                                },
                                label = "Quantum Passcode",
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = cyberRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                keyboardType = KeyboardType.Password,
                                isPassword = true
                            )

                            // ROLE SPECIFIC FIELDS
                            when (state.role) {
                                UserRole.DONOR -> DonorFields(state, onIntent)
                                UserRole.HOSPITAL -> HospitalFields(state, onIntent)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // REGISTER BUTTON
                            Button(
                                onClick = { onIntent(RegisterIntent.Submit) },
                                enabled = !state.isLoading,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = cyberRed,
                                    disabledContainerColor = cyberRed.copy(alpha = 0.4f)
                                )
                            ) {
                                if (state.isLoading) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text(
                                        text = "REGISTER NEURAL ID",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        letterSpacing = 1.sp,
                                        color = Color.White
                                    )
                                }
                            }

                            // ERROR MESSAGE
                            state.errorMessage?.let { message ->
                                Text(
                                    text = message,
                                    color = neonCrimson,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // LOGIN LINK
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Already synced? ",
                                    color = textMuted,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "Login",
                                    color = neonCrimson,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable { onNavigateToLogin() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

 */



import android.Manifest
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.social.vitadrop.domain.model.UserRole
import com.social.vitadrop.presentation.auth.register.components.DonorFields
import com.social.vitadrop.presentation.auth.register.components.HospitalFields
import com.social.vitadrop.utils.LocationHelper

// IMPORTANT: use the R class of your app "namespace" (see app/build.gradle).
import com.example.kotlinbasics.R
import com.social.vitadrop.presentation.auth.login.components.ModernRoleCard
import com.social.vitadrop.presentation.auth.register.RegisterEffect
import com.social.vitadrop.presentation.auth.register.RegisterField
import com.social.vitadrop.presentation.auth.register.RegisterIntent
import com.social.vitadrop.presentation.auth.register.RegisterState
import com.social.vitadrop.presentation.auth.register.viewmodel.RegisterViewModel

import com.social.vitadrop.presentation.components.VitaDropLoader
import com.social.vitadrop.presentation.components.VitaDropLoaderStyle

// =====================================================================
// STATEFUL WRAPPER: talks to ViewModel, location permission and effects
// =====================================================================
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onNavigateToLogin: () -> Unit,
    onRegistered: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val locationHelper = remember { LocationHelper(context) }

    fun fetchLocation() {
        locationHelper.getCurrentLocation(
            onSuccess = { lat, lng ->
                viewModel.onIntent(RegisterIntent.LocationReceived(lat, lng))
            },
            onFailure = {
                Log.e("REGISTER_GPS", it?.message ?: "Location error")
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) fetchLocation()
    }

    // Ask for / read location once when the screen opens
    LaunchedEffect(Unit) {
        if (locationHelper.hasLocationPermission()) {
            fetchLocation()
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // One-shot effects from the ViewModel (success -> navigate)
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                RegisterEffect.RegistrationSuccess -> {
                    Toast.makeText(
                        context,
                        "Registration successful. Please login.",
                        Toast.LENGTH_LONG
                    ).show()
                    onRegistered()
                }
            }
        }
    }

    RegisterContent(
        state = state,
        onIntent = viewModel::onIntent,
        onNavigateToLogin = onNavigateToLogin
    )
}

// =====================================================================
// STATELESS UI: draws state, sends intents (Minimal White Redesign)
// =====================================================================
@Composable
private fun RegisterContent(
    state: RegisterState,
    onIntent: (RegisterIntent) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    // Minimal White Color Palette
    val bgLight = Color(0xFFFAFAFC)
    val cardSurface = Color(0xFFFFFFFF)
    val primaryRed = Color(0xFFE53935)
    val accentRed = Color(0xFFD32F2F)
    val subtleBorder = Color(0xFFEEEEEE)
    val textPrimary = Color(0xFF1E293B)
    val textSecondary = Color(0xFF64748B)

    // Pulsing Heartbeat Animation
    val infiniteTransition = rememberInfiniteTransition(label = "RegisterBioPulse")
    val heartScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "HeartPulseScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgLight)
    ) {

        // High-Tech Dialog Progress Loader Overlay
        if (state.isLoading) {
            VitaDropLoader(
                style = VitaDropLoaderStyle.Dialog,
                message = "REGISTERING ACCOUNT",
                subMessage = "Creating your account credentials..."
            )
        }

        // Minimal Light Radial Ambient Glow
        Box(
            modifier = Modifier
                .size(320.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-80).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(primaryRed.copy(alpha = 0.08f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 40.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(90.dp))

            // TITLE
            Text(
                text = "Create Account",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = buildAnnotatedString {
                    append("Join VitaDrop network and ")
                    withStyle(
                        style = SpanStyle(
                            color = primaryRed,
                            fontWeight = FontWeight.SemiBold
                        )
                    ) {
                        append("save lives today.")
                    }
                },
                fontSize = 12.sp,
                color = textSecondary
            )

            Spacer(modifier = Modifier.height(18.dp))

            // ANIMATED BIO-HEART EMBLEM
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(120.dp)
                    .scale(heartScale)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(primaryRed.copy(alpha = 0.1f), Color.Transparent)
                            )
                        )
                )

                Image(
                    painter = painterResource(id = R.drawable.login_heart),
                    contentDescription = null,
                    modifier = Modifier.size(90.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // MINIMAL SWITCHER TAB
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(cardSurface)
                    .border(1.dp, subtleBorder, RoundedCornerShape(12.dp))
                    .padding(3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(9.dp))
                        .clickable { onNavigateToLogin() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Login",
                        color = textSecondary,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(9.dp))
                        .background(primaryRed),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Register",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SELECT ROLE SECTION
            Text(
                text = "SELECT ROLE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = textSecondary,
                letterSpacing = 1.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    ModernRoleCard(
                        title = "Donor",
                        icon = "🩸",
                        selected = state.role == UserRole.DONOR
                    ) {
                        onIntent(RegisterIntent.SelectRole(UserRole.DONOR))
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    ModernRoleCard(
                        title = "Hospital",
                        icon = "🏥",
                        selected = state.role == UserRole.HOSPITAL
                    ) {
                        onIntent(RegisterIntent.SelectRole(UserRole.HOSPITAL))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // DYNAMIC ROLE FORM
            AnimatedVisibility(
                visible = state.role != null,
                enter = fadeIn() + slideInVertically(initialOffsetY = { 30 }),
                exit = fadeOut()
            ) {
                if (state.role != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, subtleBorder, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardSurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            Text(
                                text = "Registering as ${state.role.name}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary,
                                letterSpacing = 0.5.sp
                            )

                            // EMAIL FIELD
                            ModernTextField(
                                value = state.email,
                                onValueChange = {
                                    onIntent(RegisterIntent.FieldChanged(RegisterField.EMAIL, it))
                                },
                                label = "Email Address",
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = null,
                                        tint = primaryRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                keyboardType = KeyboardType.Email
                            )

                            // PASSWORD FIELD
                            ModernTextField(
                                value = state.password,
                                onValueChange = {
                                    onIntent(RegisterIntent.FieldChanged(RegisterField.PASSWORD, it))
                                },
                                label = "Password",
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = primaryRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                keyboardType = KeyboardType.Password,
                                isPassword = true
                            )

                            // ROLE SPECIFIC FIELDS
                            when (state.role) {
                                UserRole.DONOR -> DonorFields(state, onIntent)
                                UserRole.HOSPITAL -> HospitalFields(state, onIntent)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // REGISTER BUTTON
                            Button(
                                onClick = { onIntent(RegisterIntent.Submit) },
                                enabled = !state.isLoading,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = primaryRed,
                                    disabledContainerColor = primaryRed.copy(alpha = 0.4f)
                                )
                            ) {
                                if (state.isLoading) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text(
                                        text = "REGISTER ACCOUNT",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        letterSpacing = 1.sp,
                                        color = Color.White
                                    )
                                }
                            }

                            // ERROR MESSAGE
                            state.errorMessage?.let { message ->
                                Text(
                                    text = message,
                                    color = accentRed,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // LOGIN LINK
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Already have an account? ",
                                    color = textSecondary,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "Login",
                                    color = primaryRed,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable { onNavigateToLogin() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}