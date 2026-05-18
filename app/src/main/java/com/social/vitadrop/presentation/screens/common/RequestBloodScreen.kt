package com.social.vitadrop.presentation.screens.common

// Modified request Screen

/*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.social.vitadrop.presentation.event.RequestEvent
import com.social.vitadrop.presentation.viewmodel.RequestViewModel

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.runtime.LaunchedEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestBloodScreen(
    navController: NavController,
    viewModel: RequestViewModel
) {

    val state by viewModel.state.collectAsState()

    Scaffold(

        containerColor = Color(0xFFFFF5F5),

        // TOP BAR
        topBar = {
            TopAppBar(

                title = {
                    Text(
                        text = "Request Blood",
                        fontWeight = FontWeight.Bold
                    )
                },

                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }

    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFF5F5))
                .padding(padding),

            contentPadding = PaddingValues(16.dp),

            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // HEADER
            item {

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFD32F2F)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {

                        Text(
                            text = "Emergency Blood Request",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Fill all required details carefully for faster donor response.",
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            // PATIENT INFORMATION
            item {

                FormCard(

                    title = "Patient Information",
                    icon = Icons.Default.Person

                ) {

                    OutlinedTextField(
                        value = state.patientName,
                        onValueChange = {
                            viewModel.onEvent(
                                RequestEvent.OnPatientNameChange(it)
                            )
                        },
                        label = {
                            Text("Patient Name")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = state.bloodGroup,
                        onValueChange = {
                            viewModel.onEvent(
                                RequestEvent.OnBloodGroupChange(it)
                            )
                        },
                        label = {
                            Text("Blood Group")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = state.unitsRequired,
                        onValueChange = {
                            viewModel.onEvent(
                                RequestEvent.OnUnitsChange(it)
                            )
                        },
                        label = {
                            Text("Units Required")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            // HOSPITAL INFORMATION
            item {

                FormCard(

                    title = "Hospital Information",
                    icon = Icons.Default.LocalHospital

                ) {

                    OutlinedTextField(
                        value = state.hospitalName,
                        onValueChange = {
                            viewModel.onEvent(
                                RequestEvent.OnHospitalNameChange(it)
                            )
                        },
                        label = {
                            Text("Hospital Name")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = state.city,
                        onValueChange = {
                            viewModel.onEvent(
                                RequestEvent.OnCityChange(it)
                            )
                        },
                        label = {
                            Text("City")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            // CONTACT INFORMATION
            item {

                FormCard(

                    title = "Contact Information",
                    icon = Icons.Default.Phone

                ) {

                    OutlinedTextField(
                        value = state.contactPerson,
                        onValueChange = {
                            viewModel.onEvent(
                                RequestEvent.OnContactPersonChange(it)
                            )
                        },
                        label = {
                            Text("Contact Person")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = state.contactNumber,
                        onValueChange = {
                            viewModel.onEvent(
                                RequestEvent.OnContactNumberChange(it)
                            )
                        },
                        label = {
                            Text("Contact Number")
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Phone, null)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            // REQUEST DETAILS
            item {

                FormCard(

                    title = "Request Details",
                    icon = Icons.Default.Warning

                ) {

                    // URGENCY
                    Text(
                        text = "Urgency Level",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        UrgencyChip(
                            title = "Low",
                            selected = state.urgency == "low"
                        ) {
                            viewModel.onEvent(
                                RequestEvent.OnUrgencyChange("low")
                            )
                        }

                        UrgencyChip(
                            title = "Medium",
                            selected = state.urgency == "medium"
                        ) {
                            viewModel.onEvent(
                                RequestEvent.OnUrgencyChange("medium")
                            )
                        }

                        UrgencyChip(
                            title = "High",
                            selected = state.urgency == "high"
                        ) {
                            viewModel.onEvent(
                                RequestEvent.OnUrgencyChange("high")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = state.description,
                        onValueChange = {
                            viewModel.onEvent(
                                RequestEvent.OnDescriptionChange(it)
                            )
                        },
                        label = {
                            Text("Description")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // EMERGENCY
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Checkbox(
                            checked = state.isEmergency,
                            onCheckedChange = {
                                viewModel.onEvent(
                                    RequestEvent.OnEmergencyToggle(it)
                                )
                            }
                        )

                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color.Red
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Mark as Emergency",
                            color = Color.Red
                        )
                    }
                }
            }

            // SUBMIT BUTTON
            item {

                Button(

                    onClick = {
                        viewModel.onEvent(
                            RequestEvent.SubmitRequest
                        )
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),

                    shape = RoundedCornerShape(14.dp),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F)
                    ),

                    enabled = !state.isLoading

                ) {

                    if (state.isLoading) {

                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )

                    } else {

                        Text(
                            text = "Submit Blood Request"
                        )
                    }
                }
            }

            // SUCCESS MESSAGE
            if (state.isSuccess) {

                item {

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE8F5E9)
                        )
                    ) {

                        Text(
                            text = "Request Submitted Successfully ✅",
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}


// FORM CARD
@Composable
fun FormCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {

    Card(
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFFD32F2F)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            content()
        }
    }
}


// URGENCY CHIP
@Composable
fun UrgencyChip(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    FilterChip(
        selected = selected,
        onClick = {
            onClick()
        },
        label = {
            Text(title)
        }
    )
}


 */
// Previous Request Screen
/*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.social.vitadrop.presentation.event.RequestEvent
import com.social.vitadrop.presentation.viewmodel.RequestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestBloodScreen(
    navController: NavController,
    viewModel: RequestViewModel
) {

    val state by viewModel.state.collectAsState()

    Scaffold(

        // TOP BAR
        topBar = {
            TopAppBar(
                title = { Text("Request Blood") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }

    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFF5F5))
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            //HEADER
            item {
                Text(
                    text = "Blood / Organ Request",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFFD32F2F)
                )

                Text(
                    text = "Fill details carefully for faster response",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            //  PATIENT INFO CARD
            item {
                FormCard {
                    OutlinedTextField(
                        value = state.patientName,
                        onValueChange = {
                            viewModel.onEvent(RequestEvent.OnPatientNameChange(it))
                        },
                        label = { Text("Patient Name") },
                        leadingIcon = { Icon(Icons.Default.Person, null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = state.bloodGroup,
                        onValueChange = {
                            viewModel.onEvent(RequestEvent.OnBloodGroupChange(it))
                        },
                        label = { Text("Blood Group") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            //  HOSPITAL INFO CARD
            item {
                FormCard {
                    OutlinedTextField(
                        value = state.hospitalName,
                        onValueChange = {
                            viewModel.onEvent(RequestEvent.OnHospitalNameChange(it))
                        },
                        label = { Text("Hospital Name") },
                        leadingIcon = { Icon(Icons.Default.LocalHospital, null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = state.city,
                        onValueChange = {
                            viewModel.onEvent(RequestEvent.OnCityChange(it))
                        },
                        label = { Text("City") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            //  CONTACT INFO CARD
            item {
                FormCard {
                    OutlinedTextField(
                        value = state.contactNumber,
                        onValueChange = {
                            viewModel.onEvent(RequestEvent.OnContactNumberChange(it))
                        },
                        label = { Text("Contact Number") },
                        leadingIcon = { Icon(Icons.Default.Phone, null) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            //  REQUEST DETAILS CARD
            item {
                FormCard {

                    OutlinedTextField(
                        value = state.unitsRequired,
                        onValueChange = {
                            viewModel.onEvent(RequestEvent.OnUnitsChange(it))
                        },
                        label = { Text("Units Required") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // EMERGENCY
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Checkbox(
                            checked = state.isEmergency,
                            onCheckedChange = {
                                viewModel.onEvent(RequestEvent.OnEmergencyToggle(it))
                            }
                        )

                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color.Red
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text("Mark as Emergency")
                    }
                }
            }

            //SUBMIT BUTTON
            item {
                Button(
                    onClick = {
                        viewModel.onEvent(RequestEvent.SubmitRequest)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F)
                    ),
                    enabled = !state.isLoading
                ) {

                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Submit Request")
                    }
                }
            }

            //SUCCESS MESSAGE
            if (state.isSuccess) {
                item {
                    Text(
                        text = "Request Submitted Successfully ✅",
                        color = Color(0xFF2E7D32)
                    )
                }
            }
        }
    }
}


//

@Composable
fun FormCard(content: @Composable ColumnScope.() -> Unit) {

    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            content()
        }
    }
}
*/



// Modified Request Screen
// Production-Level Updated Version
// Existing Functionality Preserved
// New Improvements Added with Comments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.social.vitadrop.presentation.event.RequestEvent
import com.social.vitadrop.presentation.viewmodel.RequestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestBloodScreen(
    navController: NavController,
    viewModel: RequestViewModel
) {

    val state by viewModel.state.collectAsState()

    /**
     * NEW CODE ADDED
     *
     * Snackbar Support
     */
    val snackbarHostState =
        remember {
            SnackbarHostState()
        }

    /**
     * NEW CODE ADDED
     *
     * SHOW ERROR MESSAGE
     */
    LaunchedEffect(state.errorMessage) {

        state.errorMessage?.let {

            snackbarHostState.showSnackbar(it)
        }
    }

    /**
     * NEW CODE ADDED
     *
     * SHOW SUCCESS MESSAGE
     */
    LaunchedEffect(state.isSuccess) {

        if (state.isSuccess) {

            snackbarHostState.showSnackbar(
                "Emergency Request Sent Successfully ✅"
            )
        }
    }

    Scaffold(

        containerColor = Color(0xFFFFF5F5),

        /**
         * NEW CODE ADDED
         */
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },

        // TOP BAR
        topBar = {

            TopAppBar(

                title = {

                    Text(
                        text = "Request Blood",
                        fontWeight = FontWeight.Bold
                    )
                },

                navigationIcon = {

                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {

                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }

    ) { padding ->

        LazyColumn(

            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFF5F5))
                .padding(padding),

            contentPadding = PaddingValues(16.dp),

            verticalArrangement = Arrangement.spacedBy(16.dp)

        ) {

            /**
             * HEADER
             */
            item {

                Card(

                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFD32F2F)
                    ),

                    shape = RoundedCornerShape(20.dp)

                ) {

                    Column(

                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)

                    ) {

                        Text(
                            text = "Emergency Blood Request",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White
                        )

                        Spacer(
                            modifier = Modifier.height(6.dp)
                        )

                        Text(
                            text = "Fill all required details carefully for faster donor response.",
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            /**
             * PATIENT INFORMATION
             */
            item {

                FormCard(

                    title = "Patient Information",

                    icon = Icons.Default.Person

                ) {

                    OutlinedTextField(

                        value = state.patientName,

                        onValueChange = {

                            viewModel.onEvent(
                                RequestEvent.OnPatientNameChange(it)
                            )
                        },

                        label = {
                            Text("Patient Name")
                        },

                        modifier = Modifier.fillMaxWidth(),

                        singleLine = true
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    OutlinedTextField(

                        value = state.bloodGroup,

                        onValueChange = {

                            viewModel.onEvent(
                                RequestEvent.OnBloodGroupChange(it)
                            )
                        },

                        label = {
                            Text("Blood Group")
                        },

                        modifier = Modifier.fillMaxWidth(),

                        singleLine = true
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    OutlinedTextField(

                        value = state.unitsRequired,

                        onValueChange = {

                            viewModel.onEvent(
                                RequestEvent.OnUnitsChange(it)
                            )
                        },

                        label = {
                            Text("Units Required")
                        },

                        modifier = Modifier.fillMaxWidth(),

                        singleLine = true,

                        /**
                         * NEW CODE ADDED
                         */
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                    )
                }
            }

            /**
             * HOSPITAL INFORMATION
             */
            item {

                FormCard(

                    title = "Hospital Information",

                    icon = Icons.Default.LocalHospital

                ) {

                    OutlinedTextField(

                        value = state.hospitalName,

                        onValueChange = {

                            viewModel.onEvent(
                                RequestEvent.OnHospitalNameChange(it)
                            )
                        },

                        label = {
                            Text("Hospital Name")
                        },

                        modifier = Modifier.fillMaxWidth(),

                        singleLine = true
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    OutlinedTextField(

                        value = state.city,

                        onValueChange = {

                            viewModel.onEvent(
                                RequestEvent.OnCityChange(it)
                            )
                        },

                        label = {
                            Text("City")
                        },

                        modifier = Modifier.fillMaxWidth(),

                        singleLine = true
                    )
                }
            }

            /**
             * CONTACT INFORMATION
             */
            item {

                FormCard(

                    title = "Contact Information",

                    icon = Icons.Default.Phone

                ) {

                    OutlinedTextField(

                        value = state.contactPerson,

                        onValueChange = {

                            viewModel.onEvent(
                                RequestEvent.OnContactPersonChange(it)
                            )
                        },

                        label = {
                            Text("Contact Person")
                        },

                        modifier = Modifier.fillMaxWidth(),

                        singleLine = true
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    OutlinedTextField(

                        value = state.contactNumber,

                        onValueChange = {

                            viewModel.onEvent(
                                RequestEvent.OnContactNumberChange(it)
                            )
                        },

                        label = {
                            Text("Contact Number")
                        },

                        leadingIcon = {

                            Icon(
                                Icons.Default.Phone,
                                contentDescription = null
                            )
                        },

                        modifier = Modifier.fillMaxWidth(),

                        singleLine = true,

                        /**
                         * NEW CODE ADDED
                         */
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone
                        )
                    )
                }
            }

            /**
             * REQUEST DETAILS
             */
            item {

                FormCard(

                    title = "Request Details",

                    icon = Icons.Default.Warning

                ) {

                    Text(
                        text = "Urgency Level",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Row(
                        horizontalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {

                        UrgencyChip(
                            title = "Low",
                            selected = state.urgency == "low"
                        ) {

                            viewModel.onEvent(
                                RequestEvent.OnUrgencyChange("low")
                            )
                        }

                        UrgencyChip(
                            title = "Medium",
                            selected = state.urgency == "medium"
                        ) {

                            viewModel.onEvent(
                                RequestEvent.OnUrgencyChange("medium")
                            )
                        }

                        UrgencyChip(
                            title = "High",
                            selected = state.urgency == "high"
                        ) {

                            viewModel.onEvent(
                                RequestEvent.OnUrgencyChange("high")
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    OutlinedTextField(

                        value = state.description,

                        onValueChange = {

                            viewModel.onEvent(
                                RequestEvent.OnDescriptionChange(it)
                            )
                        },

                        label = {
                            Text("Description")
                        },

                        modifier = Modifier.fillMaxWidth(),

                        minLines = 4
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    /**
                     * EMERGENCY TOGGLE
                     */
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Checkbox(

                            checked = state.isEmergency,

                            onCheckedChange = {

                                viewModel.onEvent(
                                    RequestEvent.OnEmergencyToggle(it)
                                )
                            }
                        )

                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color.Red
                        )

                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        /**
                         * UPDATED UI
                         */
                        Text(

                            text =
                                if (state.isEmergency)
                                    "Emergency Request Enabled 🚨"
                                else
                                    "Mark as Emergency",

                            color =
                                if (state.isEmergency)
                                    Color.Red
                                else
                                    Color.Gray,

                            fontWeight =
                                if (state.isEmergency)
                                    FontWeight.Bold
                                else
                                    FontWeight.Normal
                        )
                    }
                }
            }

            /**
             * SUBMIT BUTTON
             */
            item {

                Button(

                    onClick = {

                        viewModel.onEvent(
                            RequestEvent.SubmitRequest
                        )
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),

                    shape = RoundedCornerShape(14.dp),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F)
                    ),

                    enabled = !state.isLoading

                ) {

                    if (state.isLoading) {

                        CircularProgressIndicator(

                            modifier = Modifier.size(22.dp),

                            color = Color.White,

                            strokeWidth = 2.dp
                        )

                    } else {

                        Text(

                            text =
                                if (state.isLoading)
                                    "Submitting..."
                                else
                                    "Submit Blood Request"
                        )
                    }
                }
            }

            /**
             * SUCCESS MESSAGE
             */
            if (state.isSuccess) {

                item {

                    Card(

                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE8F5E9)
                        )

                    ) {

                        Text(

                            text =
                                "Request Submitted Successfully ✅",

                            color = Color(0xFF2E7D32),

                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * FORM CARD
 */
@Composable
fun FormCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {

    Card(

        shape = RoundedCornerShape(18.dp),

        elevation = CardDefaults.cardElevation(5.dp)

    ) {

        Column(

            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)

        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFFD32F2F)
                )

                Spacer(
                    modifier = Modifier.width(10.dp)
                )

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            content()
        }
    }
}

/**
 * URGENCY CHIP
 */
@Composable
fun UrgencyChip(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    FilterChip(

        selected = selected,

        onClick = {
            onClick()
        },

        label = {
            Text(title)
        }
    )
}