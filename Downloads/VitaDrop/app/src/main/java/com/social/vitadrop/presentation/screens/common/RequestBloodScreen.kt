package com.social.vitadrop.presentation.screens.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.social.vitadrop.presentation.event.RequestEvent
import com.social.vitadrop.presentation.viewmodel.RequestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestBloodScreen(
    navController: NavController,
    viewModel: RequestViewModel
) {

    val state by viewModel.state.collectAsState()

    Scaffold(

        // 🔝 TOP BAR
        topBar = {
            TopAppBar(
                title = { Text("Request Blood") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
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

            // 🧾 HEADER
            item {
                Text(
                    text = "🩸 Blood / Organ Request",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFFD32F2F)
                )

                Text(
                    text = "Fill details carefully for faster response",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            // 👤 PATIENT INFO CARD
            item {
                FormCard {
                    OutlinedTextField(
                        value = state.patientName,
                        onValueChange = {
                            viewModel.onEvent(RequestEvent.OnPatientNameChange(it))
                        },
                        label = { Text("Patient Name") },
                        leadingIcon = { Icon(Icons.Default.Person, null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = state.bloodGroup,
                        onValueChange = {
                            viewModel.onEvent(RequestEvent.OnBloodGroupChange(it))
                        },
                        label = { Text("Blood Group") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // 🏥 HOSPITAL INFO CARD
            item {
                FormCard {
                    OutlinedTextField(
                        value = state.hospitalName,
                        onValueChange = {
                            viewModel.onEvent(RequestEvent.OnHospitalNameChange(it))
                        },
                        label = { Text("Hospital Name") },
                        leadingIcon = { Icon(Icons.Default.LocalHospital, null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = state.city,
                        onValueChange = {
                            viewModel.onEvent(RequestEvent.OnCityChange(it))
                        },
                        label = { Text("City") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // 📞 CONTACT INFO CARD
            item {
                FormCard {
                    OutlinedTextField(
                        value = state.contactNumber,
                        onValueChange = {
                            viewModel.onEvent(RequestEvent.OnContactNumberChange(it))
                        },
                        label = { Text("Contact Number") },
                        leadingIcon = { Icon(Icons.Default.Phone, null) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // 🩸 REQUEST DETAILS CARD
            item {
                FormCard {

                    OutlinedTextField(
                        value = state.unitsRequired,
                        onValueChange = {
                            viewModel.onEvent(RequestEvent.OnUnitsChange(it))
                        },
                        label = { Text("Units Required") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 🚨 EMERGENCY
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Checkbox(
                            checked = state.isEmergency,
                            onCheckedChange = {
                                viewModel.onEvent(RequestEvent.OnEmergencyToggle(it))
                            }
                        )

                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color.Red
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text("Mark as Emergency")
                    }
                }
            }

            // 🚀 SUBMIT BUTTON
            item {
                Button(
                    onClick = {
                        viewModel.onEvent(RequestEvent.SubmitRequest)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F)
                    ),
                    enabled = !state.isLoading
                ) {

                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Submit Request")
                    }
                }
            }

            // ✅ SUCCESS MESSAGE
            if (state.isSuccess) {
                item {
                    Text(
                        text = "Request Submitted Successfully ✅",
                        color = Color(0xFF2E7D32)
                    )
                }
            }
        }
    }
}


//

@Composable
fun FormCard(content: @Composable ColumnScope.() -> Unit) {

    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            content()
        }
    }
}