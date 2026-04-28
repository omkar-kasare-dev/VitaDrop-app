package com.social.vitadrop.presentation.screens.donor

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.kotlinbasics.R

import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {

    Scaffold(

        // ✅ TOP BAR
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Icon(
                            imageVector = Icons.Default.Bloodtype,
                            contentDescription = "Logo",
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(32.dp) // 🔥 Increased size
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text("VitaDrop", fontSize = 25.sp)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("profile")
                    }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
                    }
                }
            )
        },

        // ✅ FLOATING BUTTON ONLY (NO BOTTOM BAR)
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("addDonor")
                },
                containerColor = Color(0xFFD32F2F)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Donor")
            }
        }

    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFFFF5F5))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // 🔥 Banner 1 (Top)
           // item { BannerImage(R.drawable.blood_banner) }

            item { StatsSection() }

            //item { EmergencyRequestSection() }
           // item { DonorScreen() }

            // 🔥 Banner 2 (Middle motivation)
           // item { BannerImage(R.drawable.blood_banner2) }

            item {
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleMedium
                )
            }

        //    item { QuickActionGrid(navController) }
        }
    }
}


// ✅ REUSABLE BANNER (USED TWICE)
@Composable
fun BannerImage(imageRes: Int) {

    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(800)) +
                slideInVertically(initialOffsetY = { -100 })
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            shape = RoundedCornerShape(16.dp)
        ) {

            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}


// ✅ STATS
@Composable
fun StatsSection() {

    val db = FirebaseFirestore.getInstance()

    var donorsCount by remember { mutableStateOf(0) }
    var hospitalsCount by remember { mutableStateOf(0) }
    var requestsCount by remember { mutableStateOf(0) }

    LaunchedEffect(true) {

        db.collection("Donors").get()
            .addOnSuccessListener { donorsCount = it.size() }

        db.collection("Hospitals").get()
            .addOnSuccessListener { hospitalsCount = it.size() }

        db.collection("Requests").get()
            .addOnSuccessListener { requestsCount = it.size() }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        StatCard("Donors", donorsCount.toString())
        StatCard("Hospitals", hospitalsCount.toString())
        StatCard("Requests", requestsCount.toString())
    }
}

@Composable
fun StatCard(title: String, value: String) {

    Card(
        modifier = Modifier
            .width(110.dp)
            .height(80.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center
        ) {

            Text(value, fontSize = 20.sp, color = Color(0xFFD32F2F))
            Text(title, fontSize = 14.sp)
        }
    }
}

/*
// ✅ EMERGENCY SECTION
@Composable
fun EmergencyRequestSection() {

    val db = FirebaseFirestore.getInstance()

    var emergencyRequests by remember {
        mutableStateOf<List<EmergencyRequest>>(emptyList())
    }

    LaunchedEffect(true) {

        db.collection("Requests")
            .whereEqualTo("emergency", true)
            .get()
            .addOnSuccessListener { result ->

                val list = mutableListOf<EmergencyRequest>()

                for (document in result) {

                    val request = EmergencyRequest(
                        patientName = document.getString("patientName") ?: "",
                        bloodGroup = document.getString("bloodGroup") ?: "",
                        hospital = document.getString("hospital") ?: "",
                        city = document.getString("city") ?: ""
                    )

                    list.add(request)
                }

                emergencyRequests = list
            }
    }

    Column {

        Text(
            text = "Emergency Blood Requests",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(modifier = Modifier.height(250.dp)) {

            items(emergencyRequests) { request ->
                EmergencyCard(request)
            }
        }
    }
}

@Composable
fun EmergencyCard(request: EmergencyRequest) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFCDD2)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = request.patientName,
                style = MaterialTheme.typography.titleMedium
            )

            Text("Blood Group: ${request.bloodGroup}")
            Text("Hospital: ${request.hospital}")
            Text("City: ${request.city}")
        }
    }
}


// ✅ QUICK ACTIONS
@Composable
fun QuickActionGrid(navController: NavController) {

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.height(220.dp)
    ) {

        item {
            QuickActionCard("Request Blood", Icons.Default.Bloodtype) {
                navController.navigate("requestBlood")
            }
        }

        item {
            QuickActionCard("Add Hospital", Icons.Default.LocalHospital) {
                navController.navigate("addHospital")
            }
        }

        item {
            QuickActionCard("Donor List", Icons.Default.People) {
                navController.navigate("donorList")
            }
        }

        item {
            QuickActionCard("Blood Camps", Icons.Default.Event) {
                navController.navigate("bloodCamp")
            }
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFFD32F2F),
                modifier = Modifier.size(36.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(title, fontSize = 14.sp)
        }
    }
}

 */