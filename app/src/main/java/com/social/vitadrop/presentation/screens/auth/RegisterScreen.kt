/*
package com.social.vitadrop.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import com.social.vitadrop.presentation.event.RegisterEvent
import com.social.vitadrop.presentation.screens.auth.components.RoleCard
import com.social.vitadrop.presentation.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel
) {

    val state by viewModel.state.collectAsState()
    val redColor = Color(0xFFD32F2F)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineMedium,
            color = redColor
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text("Select Role", fontWeight = FontWeight.Bold)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RoleCard("Donor", state.role) {
                viewModel.onEvent(RegisterEvent.UpdateField(role = "donor"))
            }

            RoleCard("Hospital", state.role) {
                viewModel.onEvent(RegisterEvent.UpdateField(role = "hospital"))
            }

            RoleCard("Admin", state.role) {
                viewModel.onEvent(RegisterEvent.UpdateField(role = "admin"))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        //  ROLE BASED FORM
        if (state.role.isNotEmpty()) {

            Card(shape = RoundedCornerShape(20.dp)) {

                Column(Modifier.padding(16.dp)) {

                    // COMMON FIELDS
                    OutlinedTextField(
                        value = state.email,
                        onValueChange = {
                            viewModel.onEvent(RegisterEvent.UpdateField(email = it))
                        },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = state.password,
                        onValueChange = {
                            viewModel.onEvent(RegisterEvent.UpdateField(password = it))
                        },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))
//<---- Section Donor---->
                    //  DONOR FIELDS
                    /*
                    if (state.role == "donor") {

                        OutlinedTextField(
                            value = state.name,
                            onValueChange = {
                                viewModel.onEvent(RegisterEvent.UpdateField(name = it))
                            },
                            label = { Text("Full Name") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = state.phone,
                            onValueChange = {
                                viewModel.onEvent(RegisterEvent.UpdateField(phone = it))
                            },
                            label = { Text("Phone") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = state.bloodGroup,
                            onValueChange = {
                                viewModel.onEvent(RegisterEvent.UpdateField(bloodGroup = it))
                            },
                            label = { Text("Blood Group") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = state.city,
                            onValueChange = {
                                viewModel.onEvent(RegisterEvent.UpdateField(city = it))
                            },
                            label = { Text("City") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                     */
                    //<---- Section Donor Modified: Start : ---->
                    //  DONOR FIELDS
                    if (state.role == "donor") {

                        OutlinedTextField(
                            value = state.fullName,
                            onValueChange = {
                                viewModel.onEvent(RegisterEvent.UpdateField(fullName = it))
                            },
                            label = { Text("Full Name") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = state.phone,
                            onValueChange = {
                                viewModel.onEvent(RegisterEvent.UpdateField(phone = it))
                            },
                            label = { Text("Phone Number") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = state.gender,
                            onValueChange = {
                                viewModel.onEvent(RegisterEvent.UpdateField(gender = it))
                            },
                            label = { Text("Gender") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = state.age,
                            onValueChange = {
                                viewModel.onEvent(RegisterEvent.UpdateField(age = it))
                            },
                            label = { Text("Age") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = state.bloodGroup,
                            onValueChange = {
                                viewModel.onEvent(RegisterEvent.UpdateField(bloodGroup = it))
                            },
                            label = { Text("Blood Group") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = state.weight,
                            onValueChange = {
                                viewModel.onEvent(RegisterEvent.UpdateField(weight = it))
                            },
                            label = { Text("Weight (KG)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = state.city,
                            onValueChange = {
                                viewModel.onEvent(RegisterEvent.UpdateField(city = it))
                            },
                            label = { Text("City") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = state.state,
                            onValueChange = {
                                viewModel.onEvent(RegisterEvent.UpdateField(state = it))
                            },
                            label = { Text("State") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = state.address,
                            onValueChange = {
                                viewModel.onEvent(RegisterEvent.UpdateField(address = it))
                            },
                            label = { Text("Address") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = state.latitude,
                            onValueChange = {
                                viewModel.onEvent(RegisterEvent.UpdateField(latitude = it))
                            },
                            label = { Text("Latitude") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = state.longitude,
                            onValueChange = {
                                viewModel.onEvent(RegisterEvent.UpdateField(longitude = it))
                            },
                            label = { Text("Longitude") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }


                    //<---- Section Donor End: ---->

                    //  HOSPITAL FIELDS

                    // HOSPITAL FIELDS
                    if (state.role == "hospital") {

                        OutlinedTextField(
                            value = state.fullName,
                            onValueChange = {
                                viewModel.onEvent(
                                    RegisterEvent.UpdateField(fullName = it)
                                )
                            },
                            label = { Text("Hospital Name") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = state.email,
                            onValueChange = {
                                viewModel.onEvent(
                                    RegisterEvent.UpdateField(email = it)
                                )
                            },
                            label = { Text("Hospital Email") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = state.phone,
                            onValueChange = {
                                viewModel.onEvent(
                                    RegisterEvent.UpdateField(phone = it)
                                )
                            },
                            label = { Text("Contact Number") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = state.licenseNumber,
                            onValueChange = {
                                viewModel.onEvent(
                                    RegisterEvent.UpdateField(licenseNumber = it)
                                )
                            },
                            label = { Text("License Number") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = state.city,
                            onValueChange = {
                                viewModel.onEvent(
                                    RegisterEvent.UpdateField(city = it)
                                )
                            },
                            label = { Text("City") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = state.state,
                            onValueChange = {
                                viewModel.onEvent(
                                    RegisterEvent.UpdateField(state = it)
                                )
                            },
                            label = { Text("State") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = state.address,
                            onValueChange = {
                                viewModel.onEvent(
                                    RegisterEvent.UpdateField(address = it)
                                )
                            },
                            label = { Text("Address") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = state.latitude,
                            onValueChange = {
                                viewModel.onEvent(
                                    RegisterEvent.UpdateField(latitude = it)
                                )
                            },
                            label = { Text("Latitude") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = state.longitude,
                            onValueChange = {
                                viewModel.onEvent(
                                    RegisterEvent.UpdateField(longitude = it)
                                )
                            },
                            label = { Text("Longitude") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }


/*
                    // 🛠 ADMIN FIELDS
                    if (state.role == "admin") {

                        OutlinedTextField(
                            value = state.name,
                            onValueChange = {
                                viewModel.onEvent(RegisterEvent.UpdateField(name = it))
                            },
                            label = { Text("Admin Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    */

                    Spacer(modifier = Modifier.height(20.dp))

                    //  REGISTER BUTTON
                    Button(
                        onClick = {
                            viewModel.onEvent(RegisterEvent.Register)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(color = Color.White)
                        } else {
                            Text("Register")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = state.message,
                        color = redColor
                    )

                    TextButton(onClick = {
                        navController.navigate("login")
                    }) {
                        Text("Already have account? Login", color = redColor)
                    }
                }
            }

                }
            }
}


 */

package com.social.vitadrop.presentation.screens.auth
import com.social.vitadrop.state.RegisterState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kotlinbasics.R

import com.social.vitadrop.presentation.event.RegisterEvent
import com.social.vitadrop.presentation.viewmodel.RegisterViewModel

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

import com.social.vitadrop.utils.LocationHelper

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel
) {

    val state by viewModel.state.collectAsState()

    // Location
    val context = LocalContext.current

    val locationHelper =
        remember {
            LocationHelper(context)
        }

    //
    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.RequestPermission()

        ) { granted ->

            if (granted) {

                locationHelper.getCurrentLocation(

                    onSuccess = { lat, lng ->

                        viewModel.onEvent(

                            RegisterEvent.UpdateField(

                                latitude =
                                    lat.toString(),

                                longitude =
                                    lng.toString()
                            )
                        )

                        Log.d(
                            "REGISTER_GPS",
                            "Lat=$lat Lng=$lng"
                        )
                    },

                    onFailure = {

                        Log.e(
                            "REGISTER_GPS",
                            it?.message ?: "Error"
                        )
                    }
                )
            }
        }
    // Launched Effect:
    LaunchedEffect(Unit) {

        if (
            locationHelper
                .hasLocationPermission()
        ) {

            locationHelper
                .getCurrentLocation(

                    onSuccess = { lat, lng ->

                        viewModel.onEvent(

                            RegisterEvent.UpdateField(

                                latitude =
                                    lat.toString(),

                                longitude =
                                    lng.toString()
                            )
                        )
                    },

                    onFailure = {

                        Log.e(
                            "REGISTER_GPS",
                            it?.message ?: ""
                        )
                    }
                )

        } else {

            locationPermissionLauncher.launch(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }
    // End Location Section

    val redPrimary = Color(0xFFD50000)
    val redDark = Color(0xFFB71C1C)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp)
            .padding(top = 55.dp, bottom = 30.dp),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // TITLE
        Text(
            text = "Create Account",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Join VitaDrop and save lives today.",
            fontSize = 15.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        // IMAGE
        Image(
            painter = painterResource(id = R.drawable.login_heart),
            contentDescription = null,
            modifier = Modifier.size(170.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(28.dp))

        // LOGIN / REGISTER TAB
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(50.dp))
                .background(Color(0xFFF5F5F5))
                .padding(4.dp)
        ) {

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(50.dp))
                    .clickable {
                        navController.navigate("login")
                    }
                    .padding(vertical = 14.dp),

                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "Login",
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(50.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                redPrimary,
                                redDark
                            )
                        )
                    )
                    .padding(vertical = 14.dp),

                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "Register",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // SELECT ROLE
        Text(
            text = "Select Role",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            ModernRoleCard(
                title = "Donor",
                icon = "🩸",
                selected = state.role == "donor"
            ) {
                viewModel.onEvent(
                    RegisterEvent.UpdateField(role = "donor")
                )
            }

            ModernRoleCard(
                title = "Hospital",
                icon = "🏥",
                selected = state.role == "hospital"
            ) {
                viewModel.onEvent(
                    RegisterEvent.UpdateField(role = "hospital")
                )
            }

            ModernRoleCard(
                title = "Admin",
                icon = "🛡",
                selected = state.role == "admin"
            ) {
                viewModel.onEvent(
                    RegisterEvent.UpdateField(role = "admin")
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ROLE BASED FORM
        if (state.role.isNotEmpty()) {

            Card(
                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(28.dp),

                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),

                elevation = CardDefaults.cardElevation(
                    defaultElevation = 5.dp
                )
            ) {

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    Text(
                        text = "Register as ${state.role.uppercase()}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = redPrimary
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // EMAIL
                    ModernTextField(
                        value = state.email,
                        onValueChange = {
                            viewModel.onEvent(
                                RegisterEvent.UpdateField(email = it)
                            )
                        },
                        label = "Email",
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // PASSWORD
                    ModernTextField(
                        value = state.password,
                        onValueChange = {
                            viewModel.onEvent(
                                RegisterEvent.UpdateField(password = it)
                            )
                        },
                        label = "Password",
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null
                            )
                        },
                        keyboardType = KeyboardType.Password
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // DONOR FIELDS
                    if (state.role == "donor") {

                        DonorFields(state, viewModel)
                    }

                    // HOSPITAL FIELDS
                    if (state.role == "hospital") {

                        HospitalFields(state, viewModel)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // REGISTER BUTTON
                    Button(
                        onClick = {
                            viewModel.onEvent(RegisterEvent.Register)
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),

                        shape = RoundedCornerShape(18.dp),

                        colors = ButtonDefaults.buttonColors(
                            containerColor = redPrimary
                        )
                    ) {

                        if (state.isLoading) {

                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )

                        } else {

                            Text(
                                text = "Create Account",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // MESSAGE
                    if (state.message.isNotEmpty()) {

                        Text(
                            text = state.message,
                            color = redPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // LOGIN
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {

                        Text(
                            text = "Already have an account? ",
                            color = Color.Gray
                        )

                        Text(
                            text = "Login",
                            color = redPrimary,
                            fontWeight = FontWeight.Bold,

                            modifier = Modifier.clickable {
                                navController.navigate("login")
                            }
                        )
                    }
                }
            }
        }
    }
}

// ================= DONOR FIELDS =================

@Composable
fun DonorFields(
    state: RegisterState,
    viewModel: RegisterViewModel
) {

    ModernTextField(
        value = state.fullName,
        onValueChange = {
            viewModel.onEvent(
                RegisterEvent.UpdateField(fullName = it)
            )
        },
        label = "Full Name",
        icon = {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null
            )
        }
    )

    Spacer(modifier = Modifier.height(14.dp))

    ModernTextField(
        value = state.phone,
        onValueChange = {
            viewModel.onEvent(
                RegisterEvent.UpdateField(phone = it)
            )
        },
        label = "Phone Number"
    )

    Spacer(modifier = Modifier.height(14.dp))

    ModernTextField(
        value = state.gender,
        onValueChange = {
            viewModel.onEvent(
                RegisterEvent.UpdateField(gender = it)
            )
        },
        label = "Gender"
    )

    Spacer(modifier = Modifier.height(14.dp))

    ModernTextField(
        value = state.age,
        onValueChange = {
            viewModel.onEvent(
                RegisterEvent.UpdateField(age = it)
            )
        },
        label = "Age"
    )

    Spacer(modifier = Modifier.height(14.dp))

    ModernTextField(
        value = state.bloodGroup,
        onValueChange = {
            viewModel.onEvent(
                RegisterEvent.UpdateField(bloodGroup = it)
            )
        },
        label = "Blood Group"
    )

    Spacer(modifier = Modifier.height(14.dp))

    ModernTextField(
        value = state.weight,
        onValueChange = {
            viewModel.onEvent(
                RegisterEvent.UpdateField(weight = it)
            )
        },
        label = "Weight (KG)"
    )

    Spacer(modifier = Modifier.height(14.dp))

    ModernTextField(
        value = state.city,
        onValueChange = {
            viewModel.onEvent(
                RegisterEvent.UpdateField(city = it)
            )
        },
        label = "City",
        icon = {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null
            )
        }
    )

    Spacer(modifier = Modifier.height(14.dp))

    ModernTextField(
        value = state.state,
        onValueChange = {
            viewModel.onEvent(
                RegisterEvent.UpdateField(state = it)
            )
        },
        label = "State"
    )

    Spacer(modifier = Modifier.height(14.dp))

    ModernTextField(
        value = state.address,
        onValueChange = {
            viewModel.onEvent(
                RegisterEvent.UpdateField(address = it)
            )
        },
        label = "Address"
    )
    Spacer(modifier = Modifier.height(14.dp))
    Text(
        text =
            "Lat : ${state.latitude}\n" +
                    "Lng : ${state.longitude}"
    )

}




// ================= HOSPITAL FIELDS =================

@Composable
fun HospitalFields(
    state: RegisterState,
    viewModel: RegisterViewModel
) {

    ModernTextField(
        value = state.fullName,
        onValueChange = {
            viewModel.onEvent(
                RegisterEvent.UpdateField(fullName = it)
            )
        },
        label = "Hospital Name"
    )

    Spacer(modifier = Modifier.height(14.dp))

    ModernTextField(
        value = state.phone,
        onValueChange = {
            viewModel.onEvent(
                RegisterEvent.UpdateField(phone = it)
            )
        },
        label = "Contact Number"
    )

    Spacer(modifier = Modifier.height(14.dp))

    ModernTextField(
        value = state.licenseNumber,
        onValueChange = {
            viewModel.onEvent(
                RegisterEvent.UpdateField(licenseNumber = it)
            )
        },
        label = "License Number"
    )

    Spacer(modifier = Modifier.height(14.dp))

    ModernTextField(
        value = state.city,
        onValueChange = {
            viewModel.onEvent(
                RegisterEvent.UpdateField(city = it)
            )
        },
        label = "City"
    )

    Spacer(modifier = Modifier.height(14.dp))

    ModernTextField(
        value = state.state,
        onValueChange = {
            viewModel.onEvent(
                RegisterEvent.UpdateField(state = it)
            )
        },
        label = "State"
    )

    Spacer(modifier = Modifier.height(14.dp))

    ModernTextField(
        value = state.address,
        onValueChange = {
            viewModel.onEvent(
                RegisterEvent.UpdateField(address = it)
            )
        },
        label = "Address"
    )
}

// ================= MODERN TEXTFIELD =================

@Composable
fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: @Composable (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text
) {

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,

        modifier = Modifier.fillMaxWidth(),

        label = {
            Text(label)
        },

        leadingIcon = icon,

        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),

        singleLine = true,

        shape = RoundedCornerShape(18.dp)
    )
}
