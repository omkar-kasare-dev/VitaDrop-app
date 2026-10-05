package com.social.vitadrop.presentation.auth.login.screen
/* Dark pallte
import com.social.vitadrop.presentation.components.VitaDropLoader
import com.social.vitadrop.presentation.components.VitaDropLoaderStyle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.social.vitadrop.domain.model.UserRole

// IMPORTANT: use the R class of your app "namespace" (see app/build.gradle).
// If your namespace is com.social.vitadrop, change this to: import com.social.vitadrop.R
import com.example.kotlinbasics.R
import com.social.vitadrop.presentation.auth.login.LoginEffect
import com.social.vitadrop.presentation.auth.login.LoginIntent
import com.social.vitadrop.presentation.auth.login.LoginState
import com.social.vitadrop.presentation.auth.login.components.ModernRoleCard
import com.social.vitadrop.presentation.auth.login.viewmodel.LoginViewModel


// =====================================================================
// STATEFUL WRAPPER: talks to the ViewModel and handles one-shot effects
// =====================================================================
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToRegister: () -> Unit,
    onLoggedIn: (UserRole) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val currentOnLoggedIn by rememberUpdatedState(onLoggedIn)

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is LoginEffect.LoggedIn -> currentOnLoggedIn(effect.role)
            }
        }
    }

    LoginContent(
        state = state,
        onIntent = viewModel::onIntent,
        onNavigateToRegister = onNavigateToRegister
    )
}

// =====================================================================
// STATELESS UI: draws the State, sends Intents back (2030 Futuristic Redesign)
// =====================================================================
@Composable
private fun LoginContent(
    state: LoginState,
    onIntent: (LoginIntent) -> Unit,
    onNavigateToRegister: () -> Unit
) {
    // Futuristic 2030 Neo-Cyber Medical Palette
    val bgDark = Color(0xFF090A0F)
    val bgSurface = Color(0xFF12141D)
    val cyberRed = Color(0xFFFF0033)
    val neonCrimson = Color(0xFFFF2A55)
    val glassBorder = Color(0xFF2A2E3D)
    val textPrimary = Color(0xFFF0F2F8)
    val textMuted = Color(0xFF7E849B)

    var passwordVisible by remember { mutableStateOf(false) }

    // Pulsing Heartbeat / Bio-Sync Infinite Animation
    val infiniteTransition = rememberInfiniteTransition(label = "BioSyncHeartbeat")
    val heartScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "HeartPulse"
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
                message = "AUTHENTICATING NEURAL ID",
                subMessage = "Verifying bio-link credentials..."
            )
        }

        // Futuristic Ambient Plasma Glows in Background
        Box(
            modifier = Modifier
                .size(320.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-80).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(cyberRed.copy(alpha = 0.25f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 44.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(90.dp))

            // TITLE
            Text(
                text = "Welcome Back",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = buildAnnotatedString {
                    append("Sync neural ID to continue ")
                    withStyle(
                        style = SpanStyle(
                            color = neonCrimson,
                            fontWeight = FontWeight.SemiBold
                        )
                    ) {
                        append("saving lives.")
                    }
                },
                fontSize = 12.sp,
                color = textMuted
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ANIMATED BIO-HEART EMBLEM
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(130.dp)
                    .scale(heartScale)
            ) {
                // Outer Pulse Ring
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
                    modifier = Modifier.size(100.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ULTRA-MINIMAL SWITCHER TAB
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
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(cyberRed, neonCrimson)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Login",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(9.dp))
                        .clickable { onNavigateToRegister() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Register",
                        color = textMuted,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // ROLE SELECTION HEADER
            Text(
                text = "AUTHENTICATION MATRIX",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = textMuted,
                letterSpacing = 1.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // ROLE SELECTION CARDS
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
                        onIntent(LoginIntent.SelectRole(UserRole.DONOR))
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    ModernRoleCard(
                        title = "Hospital",
                        icon = "🏥",
                        selected = state.role == UserRole.HOSPITAL
                    ) {
                        onIntent(LoginIntent.SelectRole(UserRole.HOSPITAL))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // FORM SECTION (Reveals smoothly upon Role selection)
            AnimatedVisibility(
                visible = state.role != null,
                enter = fadeIn() + slideInVertically(initialOffsetY = { 30 }),
                exit = fadeOut()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    // MINIMAL FUTURISTIC INPUT FIELD - EMAIL
                    OutlinedTextField(
                        value = state.email,
                        onValueChange = { onIntent(LoginIntent.EmailChanged(it)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        placeholder = {
                            Text("Neural Email / ID", fontSize = 12.sp, color = textMuted)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = cyberRed,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = bgSurface,
                            unfocusedContainerColor = bgSurface,
                            focusedBorderColor = cyberRed,
                            unfocusedBorderColor = glassBorder,
                            focusedTextColor = textPrimary,
                            unfocusedTextColor = textPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // MINIMAL FUTURISTIC INPUT FIELD - PASSWORD
                    OutlinedTextField(
                        value = state.password,
                        onValueChange = { onIntent(LoginIntent.PasswordChanged(it)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        placeholder = {
                            Text("Quantum Passcode", fontSize = 12.sp, color = textMuted)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = cyberRed,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) {
                                        Icons.Default.Visibility
                                    } else {
                                        Icons.Default.VisibilityOff
                                    },
                                    contentDescription = null,
                                    tint = textMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = bgSurface,
                            unfocusedContainerColor = bgSurface,
                            focusedBorderColor = cyberRed,
                            unfocusedBorderColor = glassBorder,
                            focusedTextColor = textPrimary,
                            unfocusedTextColor = textPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // FORGOT PASSWORD
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Reset Neural Key?",
                            color = neonCrimson,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // HIGH-TECH ACTION BUTTON
                    Button(
                        onClick = { onIntent(LoginIntent.Submit) },
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
                        Text(
                            text = "INITIALIZE SYSTEM",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = Color.White
                        )
                    }

                    // ERROR MESSAGE
                    state.errorMessage?.let { message ->
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = message,
                            color = neonCrimson,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // REGISTER LINK
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Unregistered Bio-ID? ",
                            color = textMuted,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Register",
                            color = neonCrimson,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onNavigateToRegister() }
                        )
                    }
                }
            }
        }
    }
}

 */


import com.social.vitadrop.presentation.components.VitaDropLoader
import com.social.vitadrop.presentation.components.VitaDropLoaderStyle
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.social.vitadrop.domain.model.UserRole

// IMPORTANT: use the R class of your app "namespace" (see app/build.gradle).
import com.example.kotlinbasics.R
import com.social.vitadrop.presentation.auth.login.LoginEffect
import com.social.vitadrop.presentation.auth.login.LoginIntent
import com.social.vitadrop.presentation.auth.login.LoginState
import com.social.vitadrop.presentation.auth.login.components.ModernRoleCard
import com.social.vitadrop.presentation.auth.login.viewmodel.LoginViewModel


// =====================================================================
// STATEFUL WRAPPER: talks to the ViewModel and handles one-shot effects
// =====================================================================
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToRegister: () -> Unit,
    onLoggedIn: (UserRole) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val currentOnLoggedIn by rememberUpdatedState(onLoggedIn)

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is LoginEffect.LoggedIn -> currentOnLoggedIn(effect.role)
            }
        }
    }

    LoginContent(
        state = state,
        onIntent = viewModel::onIntent,
        onNavigateToRegister = onNavigateToRegister
    )
}

// =====================================================================
// STATELESS UI: draws the State, sends Intents back (Minimal White Redesign)
// =====================================================================
@Composable
private fun LoginContent(
    state: LoginState,
    onIntent: (LoginIntent) -> Unit,
    onNavigateToRegister: () -> Unit
) {
    // Minimal White Color Palette
    val bgLight = Color(0xFFFAFAFC)
    val cardSurface = Color(0xFFFFFFFF)
    val primaryRed = Color(0xFFE53935)
    val accentRed = Color(0xFFD32F2F)
    val subtleBorder = Color(0xFFEEEEEE)
    val inputBorder = Color(0xFFE0E0E0)
    val textPrimary = Color(0xFF1E293B)
    val textSecondary = Color(0xFF64748B)

    var passwordVisible by remember { mutableStateOf(false) }

    // Pulsing Heartbeat Animation
    val infiniteTransition = rememberInfiniteTransition(label = "HeartbeatAnimation")
    val heartScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "HeartPulse"
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
                message = "AUTHENTICATING",
                subMessage = "Verifying credentials..."
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
                .padding(top = 44.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(90.dp))

            // TITLE
            Text(
                text = "Welcome Back",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = buildAnnotatedString {
                    append("Sign in to continue ")
                    withStyle(
                        style = SpanStyle(
                            color = primaryRed,
                            fontWeight = FontWeight.SemiBold
                        )
                    ) {
                        append("saving lives.")
                    }
                },
                fontSize = 12.sp,
                color = textSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ANIMATED BIO-HEART EMBLEM
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(130.dp)
                    .scale(heartScale)
            ) {
                // Outer Subtle Halo
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
                    modifier = Modifier.size(100.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

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
                        .background(primaryRed),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Login",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(9.dp))
                        .clickable { onNavigateToRegister() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Register",
                        color = textSecondary,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // ROLE SELECTION HEADER
            Text(
                text = "SELECT ROLE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = textSecondary,
                letterSpacing = 1.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // ROLE SELECTION CARDS
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
                        onIntent(LoginIntent.SelectRole(UserRole.DONOR))
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    ModernRoleCard(
                        title = "Hospital",
                        icon = "🏥",
                        selected = state.role == UserRole.HOSPITAL
                    ) {
                        onIntent(LoginIntent.SelectRole(UserRole.HOSPITAL))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // FORM SECTION (Reveals smoothly upon Role selection)
            AnimatedVisibility(
                visible = state.role != null,
                enter = fadeIn() + slideInVertically(initialOffsetY = { 30 }),
                exit = fadeOut()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    // MINIMAL INPUT FIELD - EMAIL
                    OutlinedTextField(
                        value = state.email,
                        onValueChange = { onIntent(LoginIntent.EmailChanged(it)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        placeholder = {
                            Text("Email Address", fontSize = 12.sp, color = textSecondary)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = primaryRed,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = cardSurface,
                            unfocusedContainerColor = cardSurface,
                            focusedBorderColor = primaryRed,
                            unfocusedBorderColor = inputBorder,
                            focusedTextColor = textPrimary,
                            unfocusedTextColor = textPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // MINIMAL INPUT FIELD - PASSWORD
                    OutlinedTextField(
                        value = state.password,
                        onValueChange = { onIntent(LoginIntent.PasswordChanged(it)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        placeholder = {
                            Text("Password", fontSize = 12.sp, color = textSecondary)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = primaryRed,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) {
                                        Icons.Default.Visibility
                                    } else {
                                        Icons.Default.VisibilityOff
                                    },
                                    contentDescription = null,
                                    tint = textSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = cardSurface,
                            unfocusedContainerColor = cardSurface,
                            focusedBorderColor = primaryRed,
                            unfocusedBorderColor = inputBorder,
                            focusedTextColor = textPrimary,
                            unfocusedTextColor = textPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // FORGOT PASSWORD
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Forgot Password?",
                            color = accentRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // ACTION BUTTON
                    Button(
                        onClick = { onIntent(LoginIntent.Submit) },
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
                        Text(
                            text = "LOG IN",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = Color.White
                        )
                    }

                    // ERROR MESSAGE
                    state.errorMessage?.let { message ->
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = message,
                            color = primaryRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // REGISTER LINK
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Don't have an account? ",
                            color = textSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Register",
                            color = primaryRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onNavigateToRegister() }
                        )
                    }
                }
            }
        }
    }
}