package com.social.vitadrop.presentation.screens.common



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

        // 🔝 TOP BAR
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

                // 🩸 HEADER
                Text(
                    text = "🩸 Available Requests",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFFD32F2F)
                )

                Text(
                    text = "Help patients by responding quickly",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ⏳ LOADING
                if (state.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                // ❌ ERROR
                if (!state.error.isNullOrEmpty()) {
                    Text(
                        text = state.error ?: "",
                        color = Color.Red,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                // 📋 LIST
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

            // 🔴 HEADER ROW
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
                        text = "🚨 Emergency",
                        color = Color.Red,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 🩸 BLOOD GROUP
            InfoRow("🩸 Blood Group", request.bloodGroup)

            // 🏥 HOSPITAL
            InfoRow("🏥 Hospital", request.hospitalName)

            // 📍 CITY
            InfoRow("📍 City", request.city)

            // 🧪 UNITS
            if (request.unitsRequired.isNotEmpty()) {
                InfoRow("🧪 Units", request.unitsRequired)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 📞 ACTION BUTTON (Optional but real-app feel)
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