package com.social.vitadrop.presentation.emergency.screen
/*
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

 */
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
import com.social.vitadrop.domain.model.AttendanceStatus
import com.social.vitadrop.domain.model.BloodComponent
import com.social.vitadrop.domain.model.DonationStatus
import com.social.vitadrop.domain.model.DonorResponse
import com.social.vitadrop.domain.model.RequestModel
import com.social.vitadrop.domain.model.ResponseStatus
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
    onViewContact: (String) -> Unit = {},
    // STEP 4: the donor's answer (null = no answer yet), whether it was loaded already, and
    // what to do on "Not available". Hospitals do not use them.
    myResponse: DonorResponse? = null,
    responseChecked: Boolean = true,
    onDecline: () -> Unit = {}
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

    // The number comes from the request itself (kept up to date by a Cloud Function).
    // It counts donors who are still expected: not declined, not withdrawn, not a no-show.
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
                        text = "$responseCount willing",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = textMuted
                    )
                }
            }

            // What is needed, how many units the HOSPITAL has confirmed, and until when.
            // "Willing" donors are not units: a unit counts only when the hospital confirms it.
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${BloodComponent.fromKey(request.bloodComponent).label}  ·  " +
                        "${request.unitsFulfilled} of ${request.unitsRequired} units confirmed by the hospital",
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

                DonorActions(
                    myResponse = myResponse,
                    responseChecked = responseChecked,
                    isResponding = isResponding,
                    primaryAccent = primaryAccent,
                    textDark = textDark,
                    textMuted = textMuted,
                    borderLight = borderLight,
                    onRespond = { onIntent(EmergencyIntent.Respond(requestId)) },
                    onDecline = onDecline,
                    onViewContact = { onViewContact(requestId) }
                )
            }
        }
    }
}

/**
 * What a donor sees under a request:
 *  - not answered yet      "I can donate"  /  "Not available"
 *  - said "I can donate"   what the hospital recorded so far, and "I can't donate any more"
 *                          (only until the hospital has recorded the visit)
 *  - said "not available"  "I can donate after all"
 */
@Composable
private fun DonorActions(
    myResponse: DonorResponse?,
    responseChecked: Boolean,
    isResponding: Boolean,
    primaryAccent: Color,
    textDark: Color,
    textMuted: Color,
    borderLight: Color,
    onRespond: () -> Unit,
    onDecline: () -> Unit,
    onViewContact: () -> Unit
) {
    val buttonShape = RoundedCornerShape(8.dp)

    val primaryColors = ButtonDefaults.buttonColors(
        containerColor = primaryAccent,
        disabledContainerColor = Color(0xFFF1F5F9),
        disabledContentColor = Color(0xFF94A3B8)
    )

    when {

        // Loading the answer, or sending one
        isResponding || !responseChecked -> {
            Button(
                onClick = {},
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp),
                shape = buttonShape,
                colors = primaryColors,
                contentPadding = PaddingValues(0.dp)
            ) {
                if (isResponding) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = primaryAccent
                    )
                } else {
                    Text("I can donate", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // No answer yet
        myResponse == null -> {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onRespond,
                    modifier = Modifier
                        .weight(2f)
                        .height(38.dp),
                    shape = buttonShape,
                    colors = primaryColors,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    // "I can donate" = the donor is willing. It is not a completed donation.
                    Text("I can donate", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = onDecline,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp),
                    shape = buttonShape,
                    border = BorderStroke(0.8.dp, borderLight),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Not available", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = textDark)
                }
            }
        }

        // "I can donate", and what the hospital has recorded since
        myResponse.responseStatus == ResponseStatus.ACCEPTED -> {

            val (message, isGoodNews) = when {
                myResponse.donationStatus == DonationStatus.COLLECTED ->
                    "Your donation was recorded. Thank you!" to true
                myResponse.donationStatus == DonationStatus.DEFERRED ->
                    "The hospital could not take your donation this time. Thank you for coming." to false
                myResponse.attendanceStatus == AttendanceStatus.NO_SHOW ->
                    "The hospital noted that you did not arrive." to false
                myResponse.attendanceStatus == AttendanceStatus.ARRIVED ->
                    "The hospital recorded your visit." to true
                else ->
                    "You said you can donate. Please go to the hospital: its staff will check if you can donate." to true
            }

            Text(
                text = message,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = if (isGoodNews) Color(0xFF15803D) else textMuted
            )

            if (myResponse.canChange) {
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onDecline,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp),
                    shape = buttonShape,
                    border = BorderStroke(0.8.dp, borderLight),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("I can't donate any more", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = textDark)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onViewContact,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp),
                shape = buttonShape,
                border = BorderStroke(0.8.dp, borderLight),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("View Contact Details", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = textDark)
            }
        }

        // "Not available" (declined, or withdrawn after saying yes)
        else -> {
            Text(
                text = "You told us you are not available.",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = textMuted
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onRespond,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp),
                shape = buttonShape,
                colors = primaryColors,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("I can donate after all", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}