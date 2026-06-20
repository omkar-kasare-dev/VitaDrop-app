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

            // HEADER
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

            //  Patient Name
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = request.patientName)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Hospital
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocalHospital, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = request.hospitalName)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // City
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = request.city)
            }

            Spacer(modifier = Modifier.height(12.dp))

            //CALL BUTTON
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VolunteerActivism

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

import com.social.vitadrop.domain.model.RequestModel

@Composable
fun EmergencyCard(

    request: RequestModel,

    hasResponded: Boolean,

    responseCount: Int,

    onCallClick: (String) -> Unit,

    onQuickResponse: (RequestModel) -> Unit

) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),

        shape = MaterialTheme.shapes.medium,

        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFEBEE)
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            // HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(
                                Color.Red,
                                CircleShape
                            )
                    )

                    Spacer(
                        modifier = Modifier.width(6.dp)
                    )

                    Text(
                        text = "Emergency",
                        color = Color.Red,
                        style = MaterialTheme
                            .typography
                            .labelMedium
                    )
                }

                Text(
                    text = request.bloodGroup,
                    style = MaterialTheme
                        .typography
                        .titleLarge,
                    color = Color(0xFFD32F2F)
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // PATIENT NAME
            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    Icons.Default.Person,
                    contentDescription = null
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = request.patientName
                )
            }

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            // HOSPITAL
            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    Icons.Default.LocalHospital,
                    contentDescription = null
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = request.hospitalName
                )
            }

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            // CITY
            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = request.city
                )
            }

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            // UNITS REQUIRED
            if (request.unitsRequired > 0) {

                Text(
                    text =
                        "Units Needed: ${request.unitsRequired}"
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            // RESPONSE COUNT
            Text(
                text =
                    "$responseCount Donors Responded",

                style = MaterialTheme
                    .typography
                    .bodyMedium,

                color = Color.Gray
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // ACTION BUTTONS
            Row(
                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                // I CAN HELP
                Button(

                    onClick = {
                        onQuickResponse(request)
                    },

                    enabled = !hasResponded,

                    modifier = Modifier.weight(1f),

                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                Color(0xFF2E7D32)
                        )

                ) {

                    Icon(
                        Icons.Default.VolunteerActivism,
                        contentDescription = null
                    )

                    Spacer(
                        modifier = Modifier.width(6.dp)
                    )

                    Text(
                        text =
                            if (hasResponded)
                                "Responded"
                            else
                                "I Can Help"
                    )
                }

                // CALL BUTTON
                OutlinedButton(

                    onClick = {
                        onCallClick(
                            request.contactPhone
                        )
                    },

                    modifier = Modifier.weight(1f)

                ) {

                    Icon(
                        Icons.Default.Call,
                        contentDescription = null
                    )

                    Spacer(
                        modifier = Modifier.width(6.dp)
                    )

                    Text("Call")
                }
            }

            // RESPONSE STATUS
            if (hasResponded) {

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32)
                    )

                    Spacer(
                        modifier = Modifier.width(6.dp)
                    )

                    Text(
                        text =
                            "You responded to this request",

                        color =
                            Color(0xFF2E7D32)
                    )
                }
            }
        }
    }
}