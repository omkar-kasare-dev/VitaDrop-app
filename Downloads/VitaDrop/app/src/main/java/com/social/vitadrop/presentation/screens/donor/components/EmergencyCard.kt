package com.social.vitadrop.presentation.screens.donor.components
/*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.social.vitadrop.domain.model.RequestModel

@Composable
fun EmergencyCard(
    request: RequestModel,
    onCallClick: (String) -> Unit = {}
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFEBEE)
        ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            // 🔴 HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {

                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Color.Red, CircleShape)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "Emergency",
                        color = Color.Red,
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Text(
                    text = request.bloodGroup,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFFD32F2F)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 👤 Patient Name
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = request.patientName)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 🏥 Hospital
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocalHospital, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = request.hospitalName)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 📍 City
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = request.city)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 📞 CALL BUTTON
            Button(
                onClick = { onCallClick(request.contactNumber) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD32F2F)
                )
            ) {
                Icon(Icons.Default.Call, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Call Contact")
            }
        }
    }
}

 */



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.social.vitadrop.domain.model.RequestModel

@Composable
fun EmergencyCard(
    request: RequestModel,
    onCallClick: (String) -> Unit = {},
    onQuickResponse: (RequestModel) -> Unit = {}
) {

    var isAvailable by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFEBEE)
        ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            // 🔴 HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {

                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Color.Red, CircleShape)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "Emergency",
                        color = Color.Red,
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Text(
                    text = request.bloodGroup,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFFD32F2F)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 👤 Patient Name
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = request.patientName)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 🏥 Hospital
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocalHospital, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = request.hospitalName)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 📍 City
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = request.city)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 🧪 Units
            if (request.unitsRequired.isNotEmpty()) {
                Text("🧪 Units Needed: ${request.unitsRequired}")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 🔘 AVAILABILITY TOGGLE
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {

                Switch(
                    checked = isAvailable,
                    onCheckedChange = { isAvailable = it }
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = if (isAvailable) "I'm Available to Donate" else "Mark Availability",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ⚡ QUICK ACTIONS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                // ✅ QUICK RESPONSE
                Button(
                    onClick = { onQuickResponse(request) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E7D32)
                    )
                ) {
                    Icon(Icons.Default.VolunteerActivism, null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("I Can Help")
                }

                // 📞 CALL BUTTON
                OutlinedButton(
                    onClick = { onCallClick(request.contactNumber) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Call, null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Call")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ✅ STATUS FEEDBACK
            if (isAvailable) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "You marked yourself available",
                        color = Color(0xFF2E7D32)
                    )
                }
            }
        }
    }
}