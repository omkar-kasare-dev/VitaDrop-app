package com.social.vitadrop.presentation.screens.common

/*

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.social.vitadrop.domain.model.RequestModel
import com.social.vitadrop.presentation.event.DonorDashboardEvent
import com.social.vitadrop.presentation.viewmodel.DonorDashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestListScreen(
    navController: NavController,
    viewModel: DonorDashboardViewModel
) {

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onEvent(DonorDashboardEvent.LoadRequests)
    }

    Scaffold(

        // TOP BAR
        topBar = {
            TopAppBar(
                title = { Text("Blood Requests") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }

    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFF5F5))
                .padding(padding)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {

                Spacer(modifier = Modifier.height(12.dp))

                // HEADER
                Text(
                    text = "Available Requests",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFFD32F2F)
                )

                Text(
                    text = "Help patients by responding quickly",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                // LOADING
                if (state.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                // ERROR
                if (!state.error.isNullOrEmpty()) {
                    Text(
                        text = state.error ?: "",
                        color = Color.Red,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                // LIST
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {

                    items(state.requests) { item ->
                        RequestItemCard(item)
                    }
                }
            }
        }
    }
}









@Composable
fun RequestItemCard(request: RequestModel) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = if (request.isEmergency)
                Color(0xFFFFEBEE)
            else
                Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            // HEADER ROW
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = request.patientName,
                    style = MaterialTheme.typography.titleMedium
                )

                if (request.isEmergency) {
                    Text(
                        text = " Emergency",
                        color = Color.Red,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            //  BLOOD GROUP
            InfoRow(" Blood Group", request.bloodGroup)

            // HOSPITAL
            InfoRow(" Hospital", request.hospitalName)

            //  CITY
            InfoRow(" City", request.city)

            //  UNITS
            if (request.unitsRequired.isNotEmpty()) {
                InfoRow(" Units", request.unitsRequired)
            }

            Spacer(modifier = Modifier.height(10.dp))

            //  ACTION BUTTON (Optional but real-app feel)
            if (request.contactNumber.isNotEmpty()) {
                Button(
                    onClick = { /* Call logic later */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F)
                    )
                ) {
                    Text("Contact")
                }
            }
        }
    }
}


@Composable
fun InfoRow(label: String, value: String) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }

    Spacer(modifier = Modifier.height(6.dp))
}

 */

// Updated Request List Screen



import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.social.vitadrop.domain.model.RequestModel
import com.social.vitadrop.presentation.event.DonorDashboardEvent
import com.social.vitadrop.presentation.viewmodel.DonorDashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestListScreen(
    navController: NavController,
    viewModel: DonorDashboardViewModel
) {

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onEvent(DonorDashboardEvent.LoadRequests)
    }

    Scaffold(

        containerColor = Color(0xFFF8FAFC),

        // TOP BAR
        topBar = {

            TopAppBar(

                title = {

                    Text(
                        text = "Blood Requests",
                        fontWeight = FontWeight.Bold
                    )
                },

                navigationIcon = {

                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {

                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        },

    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(padding)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {

                Spacer(modifier = Modifier.height(14.dp))

                // HEADER CARD
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFD32F2F)
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {

                        Text(
                            text = "Emergency Blood Requests",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Help save lives by responding to active donor requests quickly.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color.Green)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "${state.requests.size} Active Requests",
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // LOADING
                if (state.isLoading) {

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {

                        CircularProgressIndicator(
                            color = Color(0xFFD32F2F)
                        )
                    }
                }

                // ERROR
                if (!state.error.isNullOrEmpty()) {

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFEBEE)
                        )
                    ) {

                        Text(
                            text = state.error ?: "",
                            color = Color.Red,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }

                // EMPTY STATE
                if (
                    !state.isLoading &&
                    state.requests.isEmpty()
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 80.dp),

                        contentAlignment = Alignment.TopCenter
                    ) {

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Icon(
                                imageVector = Icons.Default.Bloodtype,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(70.dp)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "No Active Requests",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "New blood requests will appear here.",
                                color = Color.Gray
                            )
                        }
                    }
                }

                // REQUEST LIST
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {

                    items(state.requests) { item ->

                        RequestItemCard(
                            request = item
                        )
                    }
                }
            }
        }
    }
}
/*
@Composable
fun RequestItemCard(
    request: RequestModel
) {

    val urgencyColor = when (request.urgency.lowercase()) {

        "high" -> Color.Red

        "medium" -> Color(0xFFFF9800)

        else -> Color(0xFF2E7D32)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(22.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            // TOP SECTION
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = request.patientName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = request.hospitalName,
                        color = Color.Gray
                    )
                }

                Card(
                    shape = RoundedCornerShape(14.dp),

                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    )
                ) {

                    Text(
                        text = request.bloodGroup,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(
                            horizontal = 14.dp,
                            vertical = 8.dp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // REQUEST INFO
            InfoRow(
                icon = Icons.Default.LocationOn,
                label = "City",
                value = request.city
            )
/*
            InfoRow(
                icon = Icons.Default.LocalHospital,
                label = "Units Required",
                value = request.unitsRequired
            )

 */

            InfoRow(
                icon = Icons.Default.LocalHospital,
                label = "Units Required",

                //MODIFICATION:
                // Converted Long to String
                value = request.unitsRequired.toString()
            )

            InfoRow(
                icon = Icons.Default.Person,
                label = "Contact Person",
                value = request.contactPerson
            )

            InfoRow(
                icon = Icons.Default.Phone,
                label = "Phone",
                value = request.contactPhone
            )

            InfoRow(
                icon = Icons.Default.Info,
                label = "Status",
                value = request.status
            )

            Spacer(modifier = Modifier.height(14.dp))

            // URGENCY + EMERGENCY
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = urgencyColor.copy(alpha = 0.12f)
                    )
                ) {

                    Text(
                        text = request.urgency.uppercase(),
                        color = urgencyColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 6.dp
                        )
                    )
                }

                if (request.emergency) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color.Red
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "Emergency",
                            color = Color.Red,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // DESCRIPTION
            if (request.description.isNotEmpty()) {

                Spacer(modifier = Modifier.height(14.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF8FAFC)
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {

                        Text(
                            text = "Description",
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = request.description,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ACTION BUTTONS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                val context =
                    LocalContext.current

                OutlinedButton(
                    onClick = {

                        val intent = Intent(
                            Intent.ACTION_DIAL
                        ).apply {

                            data = Uri.parse(
                                "tel:${request.contactPhone}"
                            )
                        }

                        context.startActivity(intent)
                    }
                ) {

                    Icon(
                        Icons.Default.Call,
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text("Call")
                }

                Button(
                    onClick = {

                    },

                    modifier = Modifier.weight(1f),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F)
                    )
                ) {

                    Icon(
                        Icons.Default.VolunteerActivism,
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text("Donate")
                }
            }
        }
    }
}

 */

@Composable
fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {

    if (value.isEmpty()) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),

        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFFD32F2F),
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = "$label:",
            color = Color.Gray,
            modifier = Modifier.width(120.dp)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

