package com.social.vitadrop.presentation.screens.donor.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.social.vitadrop.domain.model.RequestModel

@Composable
fun EmergencyListUI(emergencyRequests: List<RequestModel>) {

    Column {

        Text(
            text = "🚨 Emergency Blood Requests",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Red
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (emergencyRequests.isEmpty()) {

            Text("No emergency requests available 🚑")

        } else {

            Column(
                modifier = Modifier
                    .heightIn(max = 325.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                emergencyRequests.forEach { request ->
                    EmergencyCard(request)
                }
            }
        }
    }
}