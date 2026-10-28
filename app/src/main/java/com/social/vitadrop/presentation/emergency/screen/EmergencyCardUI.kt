package com.social.vitadrop.presentation.emergency.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.social.vitadrop.domain.model.BloodComponent
import com.social.vitadrop.domain.model.RequestModel
import com.social.vitadrop.domain.model.UserRole
import com.social.vitadrop.presentation.components.toNeededByText
import com.social.vitadrop.presentation.emergency.EmergencyIntent
import com.social.vitadrop.presentation.emergency.EmergencyState

@Composable
fun EmergencyCardUI(
    request: RequestModel,
    uiState: EmergencyState,
    role: UserRole = UserRole.DONOR, // Added role parameter to support dynamic theme switching
    onIntent: (EmergencyIntent) -> Unit,
    onViewContact: (String) -> Unit = {}
) {
    val requestId = request.requestId

    // Dynamic Theme Color Mapping based on UserRole
    val isHospital = role == UserRole.HOSPITAL

    // Only a DONOR has a response to check. A hospital never responds, so it makes no read.
    LaunchedEffect(requestId, isHospital) {
        if (!isHospital) {
            onIntent(EmergencyIntent.RequestShown(requestId))
        }
    }

    val hasResponded = uiState.respondedRequests[requestId] ?: false
    // The number comes from the request itself (maintained by the onResponseCreated function)
    val responseCount = request.responseCount
    val isResponding = requestId in uiState.respondingRequestIds

    val primaryAccent = if (isHospital) Color(0xFF0284C7) else Color(0xFFE11D48)
    val softAccentBg = if (isHospital) Color(0xFFE0F2FE) else Color(0xFFFFF1F2)

    val textDark = Color(0xFF0F172A)
    val textMuted = Color(0xFF64748B)
    val borderLight = Color(0xFFE2E8F0)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(0.8.dp, borderLight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header Tag Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(primaryAccent)
                    )
                    Text(
                        text = if (isHospital) "HOSPITAL EMERGENCY REQUEST" else "EMERGENCY REQUEST",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryAccent,
                        letterSpacing = 0.5.sp
                    )
                }

                // Blood Group Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(softAccentBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bloodtype,
                            contentDescription = null,
                            tint = primaryAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = request.bloodGroup,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryAccent
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(thickness = 0.5.dp, color = borderLight)
            Spacer(modifier = Modifier.height(10.dp))

            // Details Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = textMuted,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = request.city,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = textDark
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = null,
                        tint = textMuted,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "$responseCount Responded",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = textMuted
                    )
                }
            }

            // STEP 3: what is needed, how many units the hospital has confirmed, and until when
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${BloodComponent.fromKey(request.bloodComponent).label}  ·  " +
                        "${request.unitsFulfilled} of ${request.unitsRequired} units arranged",
                fontSize = 11.sp,
                color = textMuted
            )

            request.requiredBy?.let { requiredBy ->
                Text(
                    text = "Needed by ${requiredBy.toNeededByText()}",
                    fontSize = 11.sp,
                    color = textMuted
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Actions
            if (isHospital) {

                // A hospital never responds to a request: it opens the details instead
                OutlinedButton(
                    onClick = { onViewContact(requestId) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(0.8.dp, borderLight),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "View Details",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = textDark
                    )
                }

            } else {

                Button(
                    onClick = { onIntent(EmergencyIntent.Respond(requestId)) },
                    enabled = !hasResponded,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryAccent,
                        disabledContainerColor = Color(0xFFF1F5F9),
                        disabledContentColor = Color(0xFF94A3B8)
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    if (isResponding) {
                        // Only the tapped card shows the spinner (the ViewModel ignores double taps)
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Text(
                            // "I can donate" = the donor is willing. It is not a completed donation.
                            text = if (hasResponded) "Responded" else "I can donate",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                if (hasResponded) {
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { onViewContact(requestId) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(0.8.dp, borderLight),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "View Contact Details",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = textDark
                        )
                    }
                }
            }
        }
    }
}