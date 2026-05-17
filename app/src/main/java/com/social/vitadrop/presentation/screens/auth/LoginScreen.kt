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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.social.vitadrop.utils.SessionManager
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
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
