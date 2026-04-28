package com.social.vitadrop.presentation.screens.common



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
        topBar = {
            TopAppBar(title = { Text("My Profile") })
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (state.isLoading) {
                CircularProgressIndicator()
                return@Column
            }

            // 👤 Avatar
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.LightGray, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(state.name, fontSize = 22.sp)
            Text(state.email, color = Color.Gray)

            Spacer(modifier = Modifier.height(20.dp))

            ProfileInfoRow("Role", state.role)
            ProfileInfoRow("Phone", state.phone)
            ProfileInfoRow("Blood Group", state.bloodGroup)
            ProfileInfoRow("City", state.city)

            Spacer(modifier = Modifier.height(30.dp))

            ProfileOption("Edit Profile", Icons.Default.Edit) {}

            ProfileOption("My Blood Requests", Icons.Default.Bloodtype) {}

            ProfileOption("Settings", Icons.Default.Settings) {}

            ProfileOption("Logout", Icons.Default.ExitToApp) {

                viewModel.logout(sessionManager) {
                    navController.navigate("login") {
                        popUpTo(0)
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(label)

        Text(value, color = Color.Gray)

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
        onClick = { onClick() }
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(icon, contentDescription = title)

            Spacer(modifier = Modifier.width(16.dp))

            Text(title)

        }

    }

}