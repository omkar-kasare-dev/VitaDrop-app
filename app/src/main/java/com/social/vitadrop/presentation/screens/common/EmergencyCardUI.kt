package com.social.vitadrop.presentation.screens.common
/*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.social.vitadrop.domain.model.RequestModel
import com.social.vitadrop.presentation.event.EmergencyEvent
import com.social.vitadrop.state.EmergencyUiState

@Composable
fun EmergencyCardUI(
    request: RequestModel,
    uiState: EmergencyUiState,
    onEvent: (EmergencyEvent) -> Unit
) {

    val requestId = request.requestId

    LaunchedEffect(requestId) {

        onEvent(
            EmergencyEvent
                .CheckAlreadyResponded(
                    requestId
                )
        )

        onEvent(
            EmergencyEvent
                .ObserveResponseCount(
                    requestId
                )
        )
    }

    val hasResponded =
        uiState.respondedRequests[
            requestId
        ] ?: false

    val responseCount =
        uiState.responseCounts[
            requestId
        ] ?: 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = request.bloodGroup,
                style =
                    MaterialTheme
                        .typography
                        .headlineSmall
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text = request.city
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text =
                    "$responseCount Donors Responded"
            )

            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )

            Button(
                onClick = {

                    onEvent(
                        EmergencyEvent
                            .RespondToRequest(
                                requestId
                            )
                    )
                },
                enabled = !hasResponded
            ) {

                Text(

                    text =
                        if (hasResponded)
                            "Already Responded"
                        else
                            "Respond"

                )
            }
        }
    }
}

 */


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.social.vitadrop.domain.model.RequestModel
import com.social.vitadrop.presentation.event.EmergencyEvent
import com.social.vitadrop.state.EmergencyUiState

@Composable
fun EmergencyCardUI(
    request: RequestModel,
    uiState: EmergencyUiState,
    onEvent: (EmergencyEvent) -> Unit,

    // NEW
    onViewContact: (String) -> Unit = {}
) {

    val requestId = request.requestId

    LaunchedEffect(requestId) {

        onEvent(
            EmergencyEvent.CheckAlreadyResponded(
                requestId
            )
        )

        onEvent(
            EmergencyEvent.ObserveResponseCount(
                requestId
            )
        )
    }

    val hasResponded =
        uiState.respondedRequests[
            requestId
        ] ?: false

    val responseCount =
        uiState.responseCounts[
            requestId
        ] ?: 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 12.dp,
                vertical = 8.dp
            ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Text(
                text = "🚨 EMERGENCY BLOOD REQUEST",
                style =
                    MaterialTheme
                        .typography
                        .titleMedium
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            HorizontalDivider()

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Text(
                    text = "🩸 Blood Group"
                )

                Text(
                    text = request.bloodGroup,
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Text(
                    text = "📍 Location"
                )

                Text(
                    text = request.city
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            HorizontalDivider()

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text = "👥"
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text =
                        "$responseCount Donors Responded",
                    style =
                        MaterialTheme
                            .typography
                            .bodyLarge
                )
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {

                    onEvent(
                        EmergencyEvent
                            .RespondToRequest(
                                requestId
                            )
                    )
                },
                enabled = !hasResponded
            ) {

                Text(

                    text =
                        if (hasResponded)
                            "Already Responded"
                        else
                            "Respond"

                )
            }

            if (hasResponded) {

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                OutlinedButton(
                    modifier =
                        Modifier.fillMaxWidth(),
                    onClick = {

                        onViewContact(
                            requestId
                        )
                    }
                ) {

                    Text(
                        text =
                            "View Contact Details"
                    )
                }
            }
        }
    }
}