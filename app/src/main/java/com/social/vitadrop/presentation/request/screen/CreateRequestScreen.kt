package com.social.vitadrop.presentation.request.screen


import android.Manifest
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.social.vitadrop.domain.model.Urgency
import com.social.vitadrop.presentation.components.FormCard
import com.social.vitadrop.presentation.request.CreateRequestEffect
import com.social.vitadrop.presentation.request.CreateRequestIntent
import com.social.vitadrop.presentation.request.CreateRequestState
import com.social.vitadrop.presentation.request.RequestField
import com.social.vitadrop.presentation.request.viewmodel.CreateRequestViewModel
import com.social.vitadrop.utils.LocationHelper

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

    Scaffold(
        containerColor = Color(0xFFFFF5F5),
        topBar = {
            TopAppBar(
                title = { Text("Request Blood", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFF5F5))
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // HEADER
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Emergency Blood Request",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Fill all required details carefully for faster donor response.",
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            // PATIENT INFORMATION
            item {
                FormCard(title = "Patient Information", icon = Icons.Default.Person) {
                    OutlinedTextField(
                        value = state.patientName,
                        onValueChange = change(RequestField.PATIENT_NAME),
                        label = { Text("Patient Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = state.bloodGroup,
                        onValueChange = change(RequestField.BLOOD_GROUP),
                        label = { Text("Blood Group (e.g. O+)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters
                        )
                    )

                    OutlinedTextField(
                        value = state.unitsRequired,
                        onValueChange = change(RequestField.UNITS),
                        label = { Text("Units Required") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }

            // LOCATION (hospital name now comes from the logged-in hospital's profile)
            item {
                FormCard(title = "Location", icon = Icons.Default.LocationOn) {
                    OutlinedTextField(
                        value = state.city,
                        onValueChange = change(RequestField.CITY),
                        label = { Text("City") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Text(
                        text = if (state.latitude.isNotBlank()) {
                            "📍 Location detected"
                        } else {
                            "⚠ Location not detected yet"
                        },
                        fontSize = 12.sp,
                        color = if (state.latitude.isNotBlank()) Color(0xFF2E7D32) else Color(0xFFEF6C00)
                    )
                }
            }

            // CONTACT INFORMATION
            item {
                FormCard(title = "Contact Information", icon = Icons.Default.Phone) {
                    OutlinedTextField(
                        value = state.contactPerson,
                        onValueChange = change(RequestField.CONTACT_PERSON),
                        label = { Text("Contact Person") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = state.contactNumber,
                        onValueChange = change(RequestField.CONTACT_NUMBER),
                        label = { Text("Contact Number") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                }
            }

            // REQUEST DETAILS
            item {
                FormCard(title = "Request Details", icon = Icons.Default.Warning) {
                    Text(
                        text = "Urgency Level",
                        style = MaterialTheme.typography.bodyMedium
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
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4
                    )

                    // EMERGENCY TOGGLE
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = state.isEmergency,
                            onCheckedChange = { onIntent(CreateRequestIntent.EmergencyToggled(it)) }
                        )

                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color.Red
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = if (state.isEmergency) {
                                "Emergency Request Enabled 🚨"
                            } else {
                                "Mark as Emergency"
                            },
                            color = if (state.isEmergency) Color.Red else Color.Gray,
                            fontWeight = if (state.isEmergency) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            // ERROR MESSAGE
            if (state.errorMessage != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                    ) {
                        Text(
                            text = state.errorMessage,
                            color = Color(0xFFC62828),
                            modifier = Modifier.padding(16.dp)
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
                        .height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Submit Blood Request")
                    }
                }
            }
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
        label = { Text(title) }
    )
}