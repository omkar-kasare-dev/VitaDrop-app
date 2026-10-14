package com.social.vitadrop.presentation.request.screen

import android.Manifest
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.social.vitadrop.domain.model.Urgency
import com.social.vitadrop.presentation.request.CreateRequestEffect
import com.social.vitadrop.presentation.request.CreateRequestIntent
import com.social.vitadrop.presentation.request.CreateRequestState
import com.social.vitadrop.presentation.request.RequestField
import com.social.vitadrop.presentation.request.viewmodel.CreateRequestViewModel
import com.social.vitadrop.utils.LocationHelper

// Real-World Design Palette Constants
private val AppBgColor = Color(0xFFF8FAFC)        // Slate 50 background
private val CardBgColor = Color(0xFFFFFFFF)       // Pure White Surface
private val BorderColor = Color(0xFFF1F5F9)       // Slate 100 soft border
private val TextPrimary = Color(0xFF0F172A)       // Slate 900 primary text
private val TextSecondary = Color(0xFF64748B)     // Slate 500 secondary label
private val PrimaryAccent = Color(0xFFE11D48)     // Deep Crimson/Rose
private val SoftBadgeBg = Color(0xFFFFF1F2)      // Light Rose tint
private val LightAccentBg = Color(0xFFFFE4E6)

// =====================================================================
// STATEFUL WRAPPER: ViewModel, location permission, one-shot effects
// =====================================================================
@Composable
fun CreateRequestScreen(
    viewModel: CreateRequestViewModel,
    onBack: () -> Unit,
    onRequestCreated: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val locationHelper = remember { LocationHelper(context) }
    val currentOnRequestCreated by rememberUpdatedState(onRequestCreated)

    fun fetchLocation() {
        locationHelper.getCurrentLocation(
            onSuccess = { lat, lng ->
                Log.d("GPS", "Lat=$lat Lng=$lng")
                viewModel.onIntent(CreateRequestIntent.LocationReceived(lat, lng))
            },
            onFailure = { Log.e("GPS", it?.message ?: "Location error") }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) fetchLocation()
    }

    // Read (or ask permission for) the location once, when the screen opens
    LaunchedEffect(Unit) {
        if (locationHelper.hasLocationPermission()) {
            fetchLocation()
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // One-shot effects from the ViewModel
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                CreateRequestEffect.RequestCreated -> {
                    Toast.makeText(
                        context,
                        "Blood request sent successfully ✅",
                        Toast.LENGTH_LONG
                    ).show()
                    currentOnRequestCreated()
                }
            }
        }
    }

    CreateRequestContent(
        state = state,
        onIntent = viewModel::onIntent,
        onBack = onBack
    )
}

// =====================================================================
// STATELESS UI: draws the State, sends Intents back
// =====================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateRequestContent(
    state: CreateRequestState,
    onIntent: (CreateRequestIntent) -> Unit,
    onBack: () -> Unit
) {
    fun change(field: RequestField): (String) -> Unit =
        { onIntent(CreateRequestIntent.FieldChanged(field, it)) }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = PrimaryAccent,
        unfocusedBorderColor = BorderColor,
        focusedLabelColor = PrimaryAccent,
        unfocusedLabelColor = TextSecondary,
        focusedContainerColor = CardBgColor,
        unfocusedContainerColor = CardBgColor,
        cursorColor = PrimaryAccent
    )

    Scaffold(
        containerColor = AppBgColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Request Blood",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CardBgColor
                )
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // HEADER
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBgColor),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, BorderColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(SoftBadgeBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Emergency,
                                    contentDescription = null,
                                    tint = PrimaryAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Emergency Blood Request",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Fill all required details carefully for faster donor response.",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            // PATIENT INFORMATION
            item {
                FormSectionCard(title = "PATIENT INFORMATION") {
                    OutlinedTextField(
                        value = state.patientName,
                        onValueChange = change(RequestField.PATIENT_NAME),
                        label = { Text("Patient Name", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = fieldColors,
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = state.bloodGroup,
                        onValueChange = change(RequestField.BLOOD_GROUP),
                        label = { Text("Blood Group (e.g. O+)", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters
                        ),
                        colors = fieldColors,
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = state.unitsRequired,
                        onValueChange = change(RequestField.UNITS),
                        label = { Text("Units Required", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = fieldColors,
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            // LOCATION
            item {
                FormSectionCard(title = "LOCATION") {
                    OutlinedTextField(
                        value = state.city,
                        onValueChange = change(RequestField.CITY),
                        label = { Text("City", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = fieldColors,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (state.latitude.isNotBlank()) Color(0xFFF0FDF4) else Color(0xFFFFFBEB),
                        border = BorderStroke(
                            1.dp,
                            if (state.latitude.isNotBlank()) Color(0xFFBBF7D0) else Color(0xFFFDE68A)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (state.latitude.isNotBlank()) Icons.Default.LocationOn else Icons.Default.LocationOff,
                                contentDescription = null,
                                tint = if (state.latitude.isNotBlank()) Color(0xFF16A34A) else Color(0xFFD97706),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (state.latitude.isNotBlank()) {
                                    "Location detected via GPS"
                                } else {
                                    "Location not detected yet"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (state.latitude.isNotBlank()) Color(0xFF15803D) else Color(0xFFB45309)
                            )
                        }
                    }
                }
            }

            // CONTACT INFORMATION
            item {
                FormSectionCard(title = "CONTACT INFORMATION") {
                    OutlinedTextField(
                        value = state.contactPerson,
                        onValueChange = change(RequestField.CONTACT_PERSON),
                        label = { Text("Contact Person", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = fieldColors,
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = state.contactNumber,
                        onValueChange = change(RequestField.CONTACT_NUMBER),
                        label = { Text("Contact Number", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = fieldColors,
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            // REQUEST DETAILS
            item {
                FormSectionCard(title = "REQUEST DETAILS") {
                    Text(
                        text = "Urgency Level",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Urgency.values().forEach { urgency ->
                            UrgencyChip(
                                title = urgency.name.lowercase().replaceFirstChar { c -> c.uppercase() },
                                selected = state.urgency == urgency
                            ) {
                                onIntent(CreateRequestIntent.UrgencyChanged(urgency))
                            }
                        }
                    }

                    OutlinedTextField(
                        value = state.description,
                        onValueChange = change(RequestField.DESCRIPTION),
                        label = { Text("Description", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        colors = fieldColors,
                        shape = RoundedCornerShape(10.dp)
                    )

                    // EMERGENCY TOGGLE
                    Surface(
                        onClick = { onIntent(CreateRequestIntent.EmergencyToggled(!state.isEmergency)) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (state.isEmergency) SoftBadgeBg else AppBgColor,
                        border = BorderStroke(
                            1.dp,
                            if (state.isEmergency) LightAccentBg else BorderColor
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Checkbox(
                                checked = state.isEmergency,
                                onCheckedChange = { onIntent(CreateRequestIntent.EmergencyToggled(it)) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = PrimaryAccent,
                                    uncheckedColor = TextSecondary
                                )
                            )

                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (state.isEmergency) PrimaryAccent else TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Text(
                                text = if (state.isEmergency) {
                                    "Emergency Request Enabled 🚨"
                                } else {
                                    "Mark as Emergency"
                                },
                                fontSize = 12.sp,
                                color = if (state.isEmergency) PrimaryAccent else TextPrimary,
                                fontWeight = if (state.isEmergency) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // ERROR MESSAGE
            if (state.errorMessage != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                        border = BorderStroke(1.dp, Color(0xFFFCA5A5))
                    ) {
                        Text(
                            text = state.errorMessage.orEmpty(),
                            color = PrimaryAccent,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }
            }

            // SUBMIT BUTTON
            item {
                Button(
                    onClick = { onIntent(CreateRequestIntent.Submit) },
                    enabled = !state.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryAccent,
                        disabledContainerColor = PrimaryAccent.copy(alpha = 0.5f)
                    )
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Submit Blood Request",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun FormSectionCard(
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
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 0.8.sp
            )

            content()
        }
    }
}

@Composable
private fun UrgencyChip(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
        },
        shape = RoundedCornerShape(8.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = SoftBadgeBg,
            selectedLabelColor = PrimaryAccent,
            containerColor = AppBgColor,
            labelColor = TextSecondary
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = BorderColor,
            selectedBorderColor = LightAccentBg,
            borderWidth = 1.dp
        )
    )
}