package com.social.vitadrop.presentation.screens.common


import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import com.social.vitadrop.domain.model.RequestModel

@Composable
fun RequestItemCard(
    request: RequestModel
) {

    val context = LocalContext.current

    val urgencyColor = when (request.urgency.lowercase()) {
        "high" -> Color(0xFFD32F2F)
        "medium" -> Color(0xFFFF9800)
        else -> Color(0xFF2E7D32)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            /* ================= TOP SECTION ================= */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {

                Column(modifier = Modifier.weight(1f)) {

                    Text(
                        text = request.patientName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E1E1E)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocalHospital,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = request.hospitalName,
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = urgencyColor.copy(alpha = 0.12f)
                    )
                ) {
                    Text(
                        text = request.bloodGroup,
                        color = urgencyColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 6.dp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Divider(color = Color(0xFFEAEAEA))

            Spacer(modifier = Modifier.height(14.dp))

            /* ================= DETAILS ================= */

            InfoRow(Icons.Default.LocationOn, "City", request.city)

            InfoRow(
                Icons.Default.LocalHospital,
                "Units Required",
                request.unitsRequired.toString()
            )

            InfoRow(Icons.Default.Person, "Contact Person", request.contactPerson)

            InfoRow(Icons.Default.Phone, "Phone", request.contactPhone)

            InfoRow(Icons.Default.Info, "Status", request.status)

            /* ================= URGENCY ================= */

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Card(
                    shape = RoundedCornerShape(50),
                    colors = CardDefaults.cardColors(
                        containerColor = urgencyColor.copy(alpha = 0.15f)
                    )
                ) {
                    Text(
                        text = request.urgency.uppercase(),
                        color = urgencyColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(
                            horizontal = 14.dp,
                            vertical = 6.dp
                        )
                    )
                }

                if (request.emergency) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(
                                Color(0xFFFFEBEE),
                                RoundedCornerShape(50)
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color.Red,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "EMERGENCY",
                            color = Color.Red,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            /* ================= DESCRIPTION ================= */

            if (request.description.isNotEmpty()) {

                Spacer(modifier = Modifier.height(14.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF6F8FA)
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {

                        Text(
                            text = "Description",
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF333333)
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

            /* ================= ACTION BUTTONS ================= */

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${request.contactPhone}")
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Call, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Call")
                }

                Button(
                    onClick = {
                        // Donate action hook
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F)
                    )
                ) {
                    Icon(Icons.Default.VolunteerActivism, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Donate")
                }
            }
        }
    }
}
