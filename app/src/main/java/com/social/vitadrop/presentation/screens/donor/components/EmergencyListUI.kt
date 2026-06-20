package com.social.vitadrop.presentation.screens.donor.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
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
fun EmergencyListUI(

    emergencyRequests: List<RequestModel>,

    respondedRequests: Map<String, Boolean>,

    responseCounts: Map<String, Int>,

    onCallClick: (String) -> Unit = {},

    onQuickResponse: (RequestModel) -> Unit = {}

) {

    Column {

        if (emergencyRequests.isEmpty()) {

            Text(
                text = "No emergency requests available",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )

        } else {

            Column(
                modifier = Modifier
                    .heightIn(max = 525.dp)
                    .verticalScroll(
                        rememberScrollState()
                    ),

                verticalArrangement =
                    Arrangement.spacedBy(10.dp)

            ) {

                emergencyRequests.forEach { request ->

                    EmergencyCard(

                        request = request,

                        hasResponded =
                            respondedRequests[
                                request.requestId
                            ] ?: false,

                        responseCount =
                            responseCounts[
                                request.requestId
                            ] ?: 0,

                        onCallClick =
                            onCallClick,

                        onQuickResponse =
                            onQuickResponse
                    )
                }
            }
        }
    }
}