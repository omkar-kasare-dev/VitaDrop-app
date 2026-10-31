package com.social.vitadrop.presentation.request.common
/*
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.social.vitadrop.domain.model.BloodComponent
import com.social.vitadrop.domain.model.RequestModel
import com.social.vitadrop.domain.model.RequestStatus
import com.social.vitadrop.presentation.components.toNeededByText

// Real-World Design Palette Constants
private val CardBgColor = Color(0xFFFFFFFF)
private val BorderColor = Color(0xFFF1F5F9)
private val TextPrimary = Color(0xFF0F172A)
private val TextSecondary = Color(0xFF64748B)
private val PrimaryAccent = Color(0xFFE11D48)
private val LightAccentBg = Color(0xFFFFE4E6)
private val SoftBadgeBg = Color(0xFFFFF1F2)

@Composable
fun RequestItemCard(
    request: RequestModel,
    modifier: Modifier = Modifier,
    onDonateClick: (RequestModel) -> Unit = {}
) {
    val context = LocalContext.current

    val urgencyColor = when (request.urgency.lowercase()) {
        "high", "critical" -> PrimaryAccent
        "medium" -> Color(0xFFD97706) // Amber/Orange 600
        else -> Color(0xFF16A34A)       // Emerald 600
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBgColor
        ),
        border = BorderStroke(1.dp, BorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            /* ================= HEADER SECTION ================= */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = request.patientName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalHospital,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = request.hospitalName,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Blood Group Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SoftBadgeBg,
                    border = BorderStroke(1.dp, LightAccentBg)
                ) {
                    Text(
                        text = request.bloodGroup,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryAccent,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            HorizontalDivider(color = BorderColor, thickness = 1.dp)

            /* ================= INFO GRID / DETAILS ================= */
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                InfoRow(icon = Icons.Default.LocationOn, label = "City", value = request.city)
                InfoRow(
                    icon = Icons.Default.Bloodtype,
                    label = "Needed",
                    value = BloodComponent.fromKey(request.bloodComponent).label
                )
                // STEP 3: "units arranged" is what the HOSPITAL confirmed, not how many donors answered
                InfoRow(
                    icon = Icons.Default.Bloodtype,
                    label = "Units",
                    value = "${request.unitsFulfilled} of ${request.unitsRequired} " +
                            "${if (request.unitsRequired == 1L) "unit" else "units"} arranged"
                )
                request.requiredBy?.let { requiredBy ->
                    InfoRow(icon = Icons.Default.Schedule, label = "Needed by", value = requiredBy.toNeededByText())
                }
                InfoRow(icon = Icons.Default.Person, label = "Contact Person", value = request.contactPerson)
                InfoRow(icon = Icons.Default.Phone, label = "Phone", value = request.contactPhone)
                InfoRow(
                    icon = Icons.Default.Info,
                    label = "Status",
                    value = RequestStatus.fromKey(request.status).label
                )
            }

            /* ================= URGENCY & EMERGENCY BADGES ================= */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Urgency Badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = urgencyColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "${request.urgency.uppercase()} URGENCY",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = urgencyColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        letterSpacing = 0.5.sp
                    )
                }

                // Emergency Badge
                if (request.emergency) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = SoftBadgeBg
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = PrimaryAccent,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "EMERGENCY",
                                fontSize = 10.sp,
                                color = PrimaryAccent,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }

            /* ================= DESCRIPTION ================= */
            if (request.description.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, BorderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Text(
                            text = "NOTES",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 0.6.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = request.description,
                            fontSize = 12.sp,
                            color = TextPrimary
                        )
                    }
                }
            }

            /* ================= ACTION BUTTONS ================= */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        if (request.contactPhone.isNotBlank()) {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${request.contactPhone}")
                            }
                            context.startActivity(intent)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextPrimary
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Call", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }

                Button(
                    onClick = { onDonateClick(request) },
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryAccent
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolunteerActivism,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("I can donate", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    if (value.isBlank()) return

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryAccent,
            modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = TextSecondary,
            modifier = Modifier.width(100.dp),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
    }
}

 */


import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.social.vitadrop.domain.model.BloodComponent
import com.social.vitadrop.domain.model.RequestModel
import com.social.vitadrop.domain.model.RequestStatus
import com.social.vitadrop.presentation.components.toNeededByText

// Real-World Design Palette Constants
private val CardBgColor = Color(0xFFFFFFFF)
private val BorderColor = Color(0xFFF1F5F9)
private val TextPrimary = Color(0xFF0F172A)
private val TextSecondary = Color(0xFF64748B)
private val PrimaryAccent = Color(0xFFE11D48)
private val LightAccentBg = Color(0xFFFFE4E6)
private val SoftBadgeBg = Color(0xFFFFF1F2)

@Composable
fun RequestItemCard(
    request: RequestModel,
    modifier: Modifier = Modifier,
    onDonateClick: (RequestModel) -> Unit = {}
) {
    val context = LocalContext.current

    val urgencyColor = when (request.urgency.lowercase()) {
        "high", "critical" -> PrimaryAccent
        "medium" -> Color(0xFFD97706) // Amber/Orange 600
        else -> Color(0xFF16A34A)       // Emerald 600
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBgColor
        ),
        border = BorderStroke(1.dp, BorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            /* ================= HEADER SECTION ================= */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = request.patientName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalHospital,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = request.hospitalName,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Blood Group Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SoftBadgeBg,
                    border = BorderStroke(1.dp, LightAccentBg)
                ) {
                    Text(
                        text = request.bloodGroup,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryAccent,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            HorizontalDivider(color = BorderColor, thickness = 1.dp)

            /* ================= INFO GRID / DETAILS ================= */
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                InfoRow(icon = Icons.Default.LocationOn, label = "City", value = request.city)
                InfoRow(
                    icon = Icons.Default.Bloodtype,
                    label = "Needed",
                    value = BloodComponent.fromKey(request.bloodComponent).label
                )
                // "confirmed" is what the HOSPITAL confirmed, not how many donors said they can donate
                InfoRow(
                    icon = Icons.Default.Bloodtype,
                    label = "Units",
                    value = "${request.unitsFulfilled} of ${request.unitsRequired} " +
                            "${if (request.unitsRequired == 1L) "unit" else "units"} confirmed by the hospital"
                )
                request.requiredBy?.let { requiredBy ->
                    InfoRow(icon = Icons.Default.Schedule, label = "Needed by", value = requiredBy.toNeededByText())
                }
                InfoRow(icon = Icons.Default.Person, label = "Contact Person", value = request.contactPerson)
                InfoRow(icon = Icons.Default.Phone, label = "Phone", value = request.contactPhone)
                InfoRow(
                    icon = Icons.Default.Info,
                    label = "Status",
                    value = RequestStatus.fromKey(request.status).label
                )
            }

            /* ================= URGENCY & EMERGENCY BADGES ================= */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Urgency Badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = urgencyColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "${request.urgency.uppercase()} URGENCY",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = urgencyColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        letterSpacing = 0.5.sp
                    )
                }

                // Emergency Badge
                if (request.emergency) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = SoftBadgeBg
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = PrimaryAccent,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "EMERGENCY",
                                fontSize = 10.sp,
                                color = PrimaryAccent,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }

            /* ================= DESCRIPTION ================= */
            if (request.description.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, BorderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Text(
                            text = "NOTES",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 0.6.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = request.description,
                            fontSize = 12.sp,
                            color = TextPrimary
                        )
                    }
                }
            }

            /* ================= ACTION BUTTONS ================= */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        if (request.contactPhone.isNotBlank()) {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${request.contactPhone}")
                            }
                            context.startActivity(intent)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextPrimary
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Call", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }

                Button(
                    onClick = { onDonateClick(request) },
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryAccent
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolunteerActivism,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("I can donate", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    if (value.isBlank()) return

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryAccent,
            modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = TextSecondary,
            modifier = Modifier.width(100.dp),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
    }
}