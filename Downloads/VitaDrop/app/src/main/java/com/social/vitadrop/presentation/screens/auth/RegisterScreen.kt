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

                    //  DONOR FIELDS
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

                    //  HOSPITAL FIELDS
                    if (state.role == "hospital") {

                        OutlinedTextField(
                            value = state.name,
                            onValueChange = {
                                viewModel.onEvent(RegisterEvent.UpdateField(name = it))
                            },
                            label = { Text("Hospital Name") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = state.phone,
                            onValueChange = {
                                viewModel.onEvent(RegisterEvent.UpdateField(phone = it))
                            },
                            label = { Text("Contact Number") },
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
                            value = state.city,
                            onValueChange = {
                                viewModel.onEvent(RegisterEvent.UpdateField(city = it))
                            },
                            label = { Text("City") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

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
