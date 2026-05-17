package com.social.vitadrop.presentation.screens.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.social.vitadrop.presentation.viewmodel.ProfileViewModel
import com.social.vitadrop.utils.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel,
    sessionManager: SessionManager
) {

    val state = viewModel.state

    LaunchedEffect(Unit) {
        viewModel.loadUser()
    }

    Scaffold(

        containerColor = Color(0xFFF5F7FB),

        topBar = {

            TopAppBar(

                title = {

                    Text(
                        text = "My Profile",
                        fontWeight = FontWeight.Bold
                    )
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }

    ) { padding ->

        if (state.isLoading) {

            Box(
                modifier = Modifier
                    .fillMaxSize(),

                contentAlignment = Alignment.Center
            ) {

                CircularProgressIndicator(
                    color = Color(0xFFD32F2F)
                )
            }

        } else {

            Column(

                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)

            ) {

                // PROFILE HEADER CARD
                Card(

                    modifier = Modifier.fillMaxWidth(),

                    shape = RoundedCornerShape(24.dp),

                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),

                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    )

                ) {

                    Column(

                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFFD32F2F),
                                        Color(0xFFEF5350)
                                    )
                                )
                            )
                            .padding(24.dp),

                        horizontalAlignment = Alignment.CenterHorizontally

                    ) {

                        // PROFILE IMAGE
                        Box(

                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .background(Color.White),

                            contentAlignment = Alignment.Center

                        ) {

                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color(0xFFD32F2F),
                                modifier = Modifier.size(60.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = state.fullName.ifEmpty { "N/A" },
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = state.email.ifEmpty { "N/A" },
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(

                            shape = RoundedCornerShape(50.dp),

                            color = Color.White.copy(alpha = 0.2f)

                        ) {

                            Text(

                                text = state.role.uppercase(),

                                modifier = Modifier.padding(
                                    horizontal = 16.dp,
                                    vertical = 6.dp
                                ),

                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // BASIC INFORMATION
                ProfileSectionCard(
                    title = "Basic Information"
                ) {

                    ProfileInfoRow("Phone", state.phone)
                    ProfileInfoRow("City", state.city)
                    ProfileInfoRow("State", state.state)
                    ProfileInfoRow("Address", state.address)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // DONOR DETAILS
                if (state.role == "donor") {

                    ProfileSectionCard(
                        title = "Donor Details"
                    ) {

                        ProfileInfoRow("Gender", state.gender)
                        ProfileInfoRow("Age", state.age)
                        ProfileInfoRow("Blood Group", state.bloodGroup)
                        ProfileInfoRow("Weight", state.weight)

                        ProfileInfoRow(
                            "Available",
                            if (state.isAvailable) "Yes" else "No"
                        )

                        ProfileInfoRow(
                            "Verification",
                            if (state.isVerified) "Verified" else "Pending"
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // HOSPITAL DETAILS
                if (state.role == "hospital") {

                    ProfileSectionCard(
                        title = "Hospital Details"
                    ) {

                        ProfileInfoRow(
                            "License Number",
                            state.licenseNumber
                        )

                        ProfileInfoRow(
                            "Verification",
                            if (state.isVerified) "Verified" else "Pending"
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // ADMIN DETAILS
                if (state.role == "admin") {

                    ProfileSectionCard(
                        title = "Admin Details"
                    ) {

                        ProfileInfoRow(
                            "Account Status",
                            if (state.isBlocked) "Blocked" else "Active"
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // OPTIONS
                Text(
                    text = "Account Settings",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(10.dp))

                ProfileOption(
                    title = "Edit Profile",
                    icon = Icons.Default.Edit
                ) {

                }

                ProfileOption(
                    title = "My Blood Requests",
                    icon = Icons.Default.Bloodtype
                ) {

                }

                ProfileOption(
                    title = "Settings",
                    icon = Icons.Default.Settings
                ) {

                }

                ProfileOption(
                    title = "Logout",
                    icon = Icons.Default.ExitToApp
                ) {

                    viewModel.logout(sessionManager) {

                        navController.navigate("login") {

                            popUpTo(0)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun ProfileSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {

    Card(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(20.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )

    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD32F2F)
            )

            Spacer(modifier = Modifier.height(12.dp))

            content()
        }
    }
}

@Composable
fun ProfileInfoRow(
    label: String,
    value: String
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {

        Text(
            text = label,
            color = Color.Gray,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = value.ifEmpty { "N/A" },
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        Divider(
            color = Color(0xFFEAEAEA)
        )
    }
}

@Composable
fun ProfileOption(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),

        shape = RoundedCornerShape(18.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),

        onClick = {
            onClick()
        }

    ) {

        Row(

            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),

            verticalAlignment = Alignment.CenterVertically

        ) {

            Box(

                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFEBEE)),

                contentAlignment = Alignment.Center

            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color(0xFFD32F2F)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}