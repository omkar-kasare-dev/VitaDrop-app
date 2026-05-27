package com.social.vitadrop.presentation.screens.donor
/*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import   androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.social.vitadrop.domain.model.DonorModel
import com.social.vitadrop.presentation.event.DonorEvent
import com.social.vitadrop.presentation.viewmodel.DonorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonorListScreen(
    navController: NavController,
    viewModel: DonorViewModel
) {

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onEvent(DonorEvent.LoadDonors)
    }

    Scaffold(

        topBar = {
            TopAppBar(
                title = { Text("Donors") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }

    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFF5F5))
                .padding(padding)
        ) {

            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            if (!state.error.isNullOrEmpty()) {
                Text(
                    text = state.error ?: "",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(state.donors) { donor ->
                    DonorCard(donor)
                }
            }
        }
    }
}



@Composable
fun DonorCard(donor: DonorModel) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (donor.isAvailable)
                Color(0xFFE8F5E9)
            else
                Color(0xFFF5F5F5)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {

        Column(modifier = Modifier.padding(14.dp)) {

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = donor.name,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = donor.bloodGroup,
                    color = Color(0xFFD32F2F)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(" ${donor.city}")
            Text(" ${donor.phone}")

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (donor.isAvailable) "Available" else "Not Available",
                color = if (donor.isAvailable) Color(0xFF2E7D32) else Color.Gray
            )
        }
    }
}

 */


// Modified Donor List Screen


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.social.vitadrop.domain.model.DonorModel
import com.social.vitadrop.presentation.event.DonorEvent
import com.social.vitadrop.presentation.viewmodel.DonorViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonorListScreen(
    navController: NavController,
    viewModel: DonorViewModel
) {

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onEvent(DonorEvent.LoadDonors)
    }

    // ================= SEARCH & FILTER STATES =================

    var searchQuery by rememberSaveable {
        mutableStateOf("")
    }

    var selectedBloodGroup by rememberSaveable {
        mutableStateOf("All")
    }

    var selectedGender by rememberSaveable {
        mutableStateOf("All")
    }

    var selectedCity by rememberSaveable {
        mutableStateOf("All")
    }

    var availableOnly by rememberSaveable {
        mutableStateOf(false)
    }

    var showFilters by remember {
        mutableStateOf(false)
    }

    val sheetState = rememberModalBottomSheetState()

    val scope = rememberCoroutineScope()

    // ================= FILTERED DONORS =================

    val filteredDonors = state.donors.filter { donor ->

        val matchesSearch =
            donor.fullName.contains(searchQuery, true) ||
                    donor.city.contains(searchQuery, true) ||
                    donor.bloodGroup.contains(searchQuery, true)

        val matchesBlood =
            selectedBloodGroup == "All" ||
                    donor.bloodGroup == selectedBloodGroup

        val matchesGender =
            selectedGender == "All" ||
                    donor.gender.equals(selectedGender, true)

        val matchesCity =
            selectedCity == "All" ||
                    donor.city.equals(selectedCity, true)

        val matchesAvailability =
            !availableOnly || donor.isAvailable

        matchesSearch &&
                matchesBlood &&
                matchesGender &&
                matchesCity &&
                matchesAvailability
    }

    Scaffold(

        containerColor = Color(0xFFFFF5F5),

        topBar = {

            TopAppBar(

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                ),

                title = {

                    Column {

                        Text(
                            text = "Blood Donors",
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "${filteredDonors.size} donors found",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                },

                navigationIcon = {

                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {

                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                },

                actions = {

                    TextButton(
                        onClick = {
                            showFilters = true
                        }
                    ) {

                        Text(
                            text = "Filters",
                            color = Color(0xFFD32F2F)
                        )
                    }
                }
            )
        }

    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFF5F5))
                .padding(padding)
        ) {

            // ================= LOADING =================

            if (state.isLoading) {

                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // ================= ERROR =================

            if (!state.error.isNullOrEmpty()) {

                Text(
                    text = state.error ?: "",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // ================= MAIN CONTENT =================

            LazyColumn(

                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 90.dp
                ),

                verticalArrangement =
                    Arrangement.spacedBy(14.dp)
            ) {

                // ================= SEARCH BAR =================

                item {

                    OutlinedTextField(

                        value = searchQuery,

                        onValueChange = {
                            searchQuery = it
                        },

                        modifier = Modifier.fillMaxWidth(),

                        placeholder = {
                            Text("Search donor, city or blood group")
                        },

                        leadingIcon = {

                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        },

                        shape = RoundedCornerShape(18.dp),

                        singleLine = true
                    )
                }

                // ================= ACTIVE FILTERS =================

                item {

                    Row(
                        horizontalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {

                        if (selectedBloodGroup != "All") {
                            ActiveFilterChip(selectedBloodGroup)
                        }

                        if (selectedGender != "All") {
                            ActiveFilterChip(selectedGender)
                        }

                        if (selectedCity != "All") {
                            ActiveFilterChip(selectedCity)
                        }

                        if (availableOnly) {
                            ActiveFilterChip("Available")
                        }
                    }
                }

                // ================= EMPTY STATE =================

                if (filteredDonors.isEmpty()) {

                    item {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 80.dp),

                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {

                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = Color.LightGray,
                                modifier = Modifier.size(70.dp)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "No donors found",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Try changing filters",
                                color = Color.Gray
                            )
                        }
                    }
                }

                // ================= DONOR LIST =================

                items(filteredDonors) { donor ->

                    DonorCard(
                        donor = donor
                    )
                }
            }
        }

        // ================= FILTER BOTTOM SHEET =================

        if (showFilters) {

            ModalBottomSheet(

                onDismissRequest = {
                    showFilters = false
                },

                sheetState = sheetState
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        text = "Filters",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    CompactDropdownFilter(

                        label = "Blood Group",

                        selectedValue = selectedBloodGroup,

                        options = listOf(
                            "All",
                            "A+",
                            "A-",
                            "B+",
                            "B-",
                            "O+",
                            "O-",
                            "AB+",
                            "AB-"
                        ),

                        onValueSelected = {
                            selectedBloodGroup = it
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CompactDropdownFilter(

                        label = "Gender",

                        selectedValue = selectedGender,

                        options = listOf(
                            "All",
                            "Male",
                            "Female"
                        ),

                        onValueSelected = {
                            selectedGender = it
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val cities =
                        listOf("All") +
                                state.donors.map {
                                    it.city.trim()
                                }.distinct()

                    CompactDropdownFilter(

                        label = "City",

                        selectedValue = selectedCity,

                        options = cities,

                        onValueSelected = {
                            selectedCity = it
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Checkbox(
                            checked = availableOnly,

                            onCheckedChange = {
                                availableOnly = it
                            }
                        )

                        Text(
                            text = "Available donors only"
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {

                        OutlinedButton(

                            onClick = {

                                selectedBloodGroup = "All"
                                selectedGender = "All"
                                selectedCity = "All"
                                availableOnly = false
                                searchQuery = ""
                            },

                            modifier = Modifier.weight(1f)
                        ) {

                            Text("Clear")
                        }

                        Button(

                            onClick = {

                                scope.launch {
                                    sheetState.hide()
                                    showFilters = false
                                }
                            },

                            modifier = Modifier.weight(1f),

                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFD32F2F)
                            )
                        ) {

                            Text("Apply")
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}

// ================= ACTIVE FILTER CHIP =================

@Composable
fun ActiveFilterChip(
    text: String
) {

    Surface(
        shape = RoundedCornerShape(50.dp),
        color = Color(0xFFFFEBEE)
    ) {

        Text(
            text = text,
            color = Color(0xFFD32F2F),

            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 6.dp
            )
        )
    }
}

// ================= DROPDOWN FILTER =================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompactDropdownFilter(
    label: String,
    selectedValue: String,
    options: List<String>,
    onValueSelected: (String) -> Unit
) {

    var expanded by remember {
        mutableStateOf(false)
    }

    ExposedDropdownMenuBox(

        expanded = expanded,

        onExpandedChange = {
            expanded = !expanded
        }
    ) {

        OutlinedTextField(

            value = selectedValue,

            onValueChange = {},

            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),

            readOnly = true,

            label = {
                Text(label)
            },

            trailingIcon = {

                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },

            shape = RoundedCornerShape(16.dp)
        )

        ExposedDropdownMenu(

            expanded = expanded,

            onDismissRequest = {
                expanded = false
            }
        ) {

            options.forEach { option ->

                DropdownMenuItem(

                    text = {
                        Text(option)
                    },

                    onClick = {

                        onValueSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

// ================= DONOR CARD =================

@Composable
fun DonorCard(
    donor: DonorModel
) {

    Card(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(20.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFEBEE)),

                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {

                        Text(
                            text = donor.fullName,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                text = "${donor.city}, ${donor.state}",
                                color = Color.Gray
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFEBEE)
                ) {

                    Row(
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        ),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Default.Bloodtype,
                            contentDescription = null,
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = donor.bloodGroup,
                            color = Color(0xFFD32F2F),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Divider()

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                InfoItem(
                    title = "Gender",
                    value = donor.gender
                )

                InfoItem(
                    title = "Age",
                    value = donor.age.toString()
                )

                InfoItem(
                    title = "Weight",
                    value = "${donor.weight} Kg"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = null,
                    tint = Color(0xFFD32F2F),
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = donor.phone
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Surface(

                    shape = RoundedCornerShape(50.dp),

                    color =
                        if (donor.isAvailable)
                            Color(0xFFE8F5E9)
                        else
                            Color(0xFFFFEBEE)
                ) {

                    Text(

                        text =
                            if (donor.isAvailable)
                                "Available"
                            else
                                "Unavailable",

                        color =
                            if (donor.isAvailable)
                                Color(0xFF2E7D32)
                            else
                                Color.Red,

                        modifier = Modifier.padding(
                            horizontal = 14.dp,
                            vertical = 6.dp
                        )
                    )
                }

                if (donor.isVerified) {

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "Verified",
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }
        }
    }
}

// ================= INFO ITEM =================

@Composable
fun InfoItem(
    title: String,
    value: String
) {

    Column {

        Text(
            text = title,
            color = Color.Gray,
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = value,
            fontWeight = FontWeight.Medium
        )
    }
}
/*
@Composable
fun DonorCard(
    donor: DonorModel
) {

    Card(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(20.dp),

        colors = CardDefaults.cardColors(

            containerColor =
                if (donor.isAvailable)
                    Color.White
                else
                    Color(0xFFF5F5F5)
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            // TOP SECTION
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFEBEE)),

                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.size(12.dp))

                    Column {

                        Text(
                            text = donor.fullName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(modifier = Modifier.size(4.dp))

                            Text(
                                text = "${donor.city}, ${donor.state}",
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFEBEE)
                ) {

                    Row(
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Default.Bloodtype,
                            contentDescription = null,
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = donor.bloodGroup,
                            color = Color(0xFFD32F2F),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Divider()

            Spacer(modifier = Modifier.height(14.dp))

            // DETAILS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column {

                    Text(
                        text = "Gender",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Text(
                        text = donor.gender
                    )
                }

                Column {

                    Text(
                        text = "Age",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Text(
                        text = donor.age.toString()
                    )
                }

                Column {

                    Text(
                        text = "Weight",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Text(
                        text = "${donor.weight} Kg"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // PHONE
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = null,
                    tint = Color(0xFFD32F2F),
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = donor.phone
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // STATUS SECTION
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Surface(
                    shape = RoundedCornerShape(50.dp),

                    color =
                        if (donor.isAvailable)
                            Color(0xFFE8F5E9)
                        else
                            Color(0xFFFFEBEE)
                ) {

                    Text(
                        text =
                            if (donor.isAvailable)
                                "Available"
                            else
                                "Unavailable",

                        color =
                            if (donor.isAvailable)
                                Color(0xFF2E7D32)
                            else
                                Color.Red,

                        modifier = Modifier.padding(
                            horizontal = 14.dp,
                            vertical = 6.dp
                        )
                    )
                }

                if (donor.isVerified) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "Verified",
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }
        }
    }
}

 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompactDropdownFilter(

    modifier: Modifier = Modifier,

    label: String,

    selectedValue: String,

    options: List<String>,

    onValueSelected: (String) -> Unit
) {

    var expanded by remember {
        mutableStateOf(false)
    }

    ExposedDropdownMenuBox(

        expanded = expanded,

        onExpandedChange = {
            expanded = !expanded
        }
    ) {

        OutlinedTextField(

            value = selectedValue,

            onValueChange = {},

            modifier = modifier.menuAnchor(),

            readOnly = true,

            singleLine = true,

            label = {
                Text(label)
            },

            trailingIcon = {

                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },

            shape = RoundedCornerShape(16.dp),

            colors = OutlinedTextFieldDefaults.colors(

                focusedBorderColor = Color(0xFFD32F2F),

                focusedLabelColor = Color(0xFFD32F2F)
            )
        )

        ExposedDropdownMenu(

            expanded = expanded,

            onDismissRequest = {
                expanded = false
            }
        ) {

            options.forEach { option ->

                DropdownMenuItem(

                    text = {
                        Text(option)
                    },

                    onClick = {

                        onValueSelected(option)

                        expanded = false
                    }
                )
            }
        }
    }
}