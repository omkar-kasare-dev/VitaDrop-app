/*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import com.social.vitadrop.presentation.screens.auth.components.RoleCard
import com.social.vitadrop.presentation.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel
) {

    val state = viewModel.state
    val redColor = Color(0xFFD32F2F)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) 
                .imePadding()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "VitaDrop Login",
                style = MaterialTheme.typography.headlineMedium,
                color = redColor
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text("Select Role", fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                RoleCard("Donor", state.role) {
                    viewModel.onRoleSelected("donor")
                }

                RoleCard("Hospital", state.role) {
                    viewModel.onRoleSelected("hospital")
                }

                RoleCard("Admin", state.role) {
                    viewModel.onRoleSelected("admin")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (state.role.isNotEmpty()) {

                Card(
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(modifier = Modifier.padding(16.dp)) {

                        Text(
                            text = "Login as ${state.role.uppercase()}",
                            fontWeight = FontWeight.Bold,
                            color = redColor
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = state.email,
                            onValueChange = viewModel::onEmailChange,
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = state.password,
                            onValueChange = viewModel::onPasswordChange,
                            label = { Text("Password") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                viewModel.login { role ->
                                    navController.navigate("dashboard/$role") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(30),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = redColor
                            )
                        ) {
                            Text("Login")
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (state.isLoading) {
                            CircularProgressIndicator()
                        }

                        Text(text = state.message, color = redColor)

                        Spacer(modifier = Modifier.height(10.dp))

                        TextButton(
                            onClick = {
                                navController.navigate("register")
                            }
                        ) {
                            Text("New User? Register", color = redColor)
                        }
                    }
                }
            }
        }
    }
}

 */

package com.social.vitadrop.presentation.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kotlinbasics.R
import com.social.vitadrop.presentation.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel
) {

    val state = viewModel.state

    val redPrimary = Color(0xFFD50000)
    val redDark = Color(0xFFB71C1C)

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 60.dp, bottom = 30.dp),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // TITLE
            Text(
                text = "Welcome Back!",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = buildAnnotatedString {

                    append("Login to continue ")

                    withStyle(
                        style = SpanStyle(
                            color = redPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("saving lives.")
                    }
                },
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(30.dp))

            // HEART IMAGE
            Image(
                painter = painterResource(id = R.drawable.login_heart),
                contentDescription = null,
                modifier = Modifier.size(180.dp),
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

                // LOGIN
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
                        text = "Login",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                // REGISTER
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(50.dp))
                        .clickable {
                            navController.navigate("register")
                        }
                        .padding(vertical = 14.dp),

                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "Register",
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // ================= ROLE SELECTION =================

            Text(
                text = "Select Role",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ){

                ModernRoleCard(
                    title = "Donor",
                    icon = "🩸",
                    selected = state.role == "donor"
                ) {
                    viewModel.onRoleSelected("donor")
                }

                ModernRoleCard(
                    title = "Hospital",
                    icon = "🏥",
                    selected = state.role == "hospital"
                ) {
                    viewModel.onRoleSelected("hospital")
                }

                ModernRoleCard(
                    title = "Admin",
                    icon = "🛡",
                    selected = state.role == "admin"
                ) {
                    viewModel.onRoleSelected("admin")
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // SHOW LOGIN FORM ONLY AFTER ROLE SELECTED
            if (state.role.isNotEmpty()) {

                // EMAIL FIELD
                OutlinedTextField(
                    value = state.email,
                    onValueChange = viewModel::onEmailChange,

                    modifier = Modifier.fillMaxWidth(),

                    placeholder = {
                        Text("Email or Phone Number")
                    },

                    leadingIcon = {

                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    },

                    shape = RoundedCornerShape(18.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(18.dp))

                // PASSWORD FIELD
                OutlinedTextField(
                    value = state.password,
                    onValueChange = viewModel::onPasswordChange,

                    modifier = Modifier.fillMaxWidth(),

                    placeholder = {
                        Text("Password")
                    },

                    leadingIcon = {

                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    },

                    trailingIcon = {

                        IconButton(
                            onClick = {
                                passwordVisible = !passwordVisible
                            }
                        ) {

                            Icon(
                                imageVector =
                                    if (passwordVisible)
                                        Icons.Default.Visibility
                                    else
                                        Icons.Default.VisibilityOff,

                                contentDescription = null
                            )
                        }
                    },

                    visualTransformation =
                        if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),

                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),

                    shape = RoundedCornerShape(18.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // FORGOT PASSWORD
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {

                    Text(
                        text = "Forgot Password?",
                        color = redPrimary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable {

                        }
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // LOGIN BUTTON
                Button(
                    onClick = {

                        viewModel.login { role ->

                            navController.navigate("dashboard/$role") {

                                popUpTo("login") {
                                    inclusive = true
                                }
                            }
                        }
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),

                    shape = RoundedCornerShape(18.dp),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = redPrimary
                    )
                ) {

                    if (state.isLoading) {

                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(24.dp)
                        )

                    } else {

                        Text(
                            text = "Login",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // ERROR / SUCCESS MESSAGE
                if (state.message.isNotEmpty()) {

                    Text(
                        text = state.message,
                        color = redPrimary
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                // SOCIAL LOGIN
                Text(
                    text = "or continue with",
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
/*
                    SocialLoginButton(
                        icon = R.drawable.google
                    )

                    SocialLoginButton(
                        icon = R.drawable.facebook
                    )

 */
                }

                Spacer(modifier = Modifier.height(30.dp))

                // REGISTER TEXT
                Row {

                    Text(
                        text = "Don't have an account? ",
                        color = Color.DarkGray
                    )

                    Text(
                        text = "Register",
                        color = redPrimary,
                        fontWeight = FontWeight.Bold,

                        modifier = Modifier.clickable {
                            navController.navigate("register")
                        }
                    )
                }
            }
        }
    }
}

// ================= ROLE CARD =================

@Composable
fun ModernRoleCard(
    title: String,
    icon: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    val primaryRed = Color(0xFFD50000)

    Card(
        modifier = Modifier
            .width(90.dp)
            .height(85.dp)
            .clickable {
                onClick()
            },

        shape = RoundedCornerShape(18.dp),

        colors = CardDefaults.cardColors(
            containerColor =
                if (selected)
                    primaryRed.copy(alpha = 0.12f)
                else
                    Color.White
        ),

        border =
            if (selected)
                ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.horizontalGradient(
                        listOf(primaryRed, primaryRed)
                    )
                )
            else
                null,

        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),

            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = icon,
                fontSize = 22.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = title,

                fontSize = 13.sp,

                color =
                    if (selected)
                        primaryRed
                    else
                        Color.DarkGray,

                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ================= SOCIAL LOGIN BUTTON =================

@Composable
fun SocialLoginButton(
    icon: Int
) {

    Card(
        modifier = Modifier
            .size(width = 130.dp, height = 65.dp)
            .clickable {

            },

        shape = RoundedCornerShape(18.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}
