package com.social.vitadrop.presentation.donor.components


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.social.vitadrop.domain.model.DonorModel

// Real-World Design Palette Constants
private val CardBgColor = Color(0xFFFFFFFF)       // Pure White Surface
private val BorderColor = Color(0xFFF1F5F9)       // Slate 100 soft border
private val TextPrimary = Color(0xFF0F172A)       // Slate 900 primary text
private val TextSecondary = Color(0xFF64748B)     // Slate 500 secondary label
private val PrimaryAccent = Color(0xFFE11D48)     // Deep Crimson/Rose
private val SoftBadgeBg = Color(0xFFFFF1F2)      // Light Rose tint
private val LightAccentBg = Color(0xFFFFE4E6)

@Composable
fun DonorCard(
    donor: DonorModel
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBgColor),
        border = BorderStroke(1.dp, BorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // NAME / CITY / BLOOD GROUP
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(SoftBadgeBg)
                            .borderBorderStrokeCompat(1.dp, LightAccentBg, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = PrimaryAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = donor.fullName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(13.dp)
                            )

                            Spacer(modifier = Modifier.width(3.dp))

                            Text(
                                text = "${donor.city}, ${donor.state}",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SoftBadgeBg,
                    border = BorderStroke(1.dp, LightAccentBg)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bloodtype,
                            contentDescription = null,
                            tint = PrimaryAccent,
                            modifier = Modifier.size(14.dp)
                        )

                        Spacer(modifier = Modifier.width(3.dp))

                        Text(
                            text = donor.bloodGroup,
                            fontSize = 13.sp,
                            color = PrimaryAccent,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            HorizontalDivider(color = BorderColor, thickness = 1.dp)

            // GENDER / AGE / WEIGHT
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoItem(title = "Gender", value = donor.gender)
                InfoItem(title = "Age", value = donor.age.toString())
                InfoItem(title = "Weight", value = "${donor.weight} Kg")
            }

            // PHONE
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = null,
                    tint = PrimaryAccent,
                    modifier = Modifier.size(14.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = donor.phone,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
            }

            // AVAILABILITY / VERIFIED
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (donor.isAvailable) Color(0xFFF0FDF4) else Color(0xFFFEF2F2),
                    border = BorderStroke(
                        1.dp,
                        if (donor.isAvailable) Color(0xFFBBF7D0) else Color(0xFFFCA5A5)
                    )
                ) {
                    Text(
                        text = if (donor.isAvailable) "Available" else "Unavailable",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (donor.isAvailable) Color(0xFF16A34A) else PrimaryAccent,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        letterSpacing = 0.5.sp
                    )
                }

                if (donor.isVerified) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = Color(0xFF16A34A),
                            modifier = Modifier.size(14.dp)
                        )

                        Spacer(modifier = Modifier.width(3.dp))

                        Text(
                            text = "Verified",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF16A34A)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InfoItem(
    title: String,
    value: String
) {
    Column {
        Text(
            text = title.uppercase(),
            fontSize = 10.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.6.sp
        )

        Spacer(modifier = Modifier.height(1.dp))

        Text(
            text = value.ifEmpty { "N/A" },
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
    }
}

// Helper modifier extension for smooth border application
private fun Modifier.borderBorderStrokeCompat(
    width: androidx.compose.ui.unit.Dp,
    color: Color,
    shape: androidx.compose.ui.graphics.Shape
) = this.border(width, color, shape)