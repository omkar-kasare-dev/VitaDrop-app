package com.social.vitadrop.presentation.donor.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.social.vitadrop.presentation.components.CompactDropdownFilter
import com.social.vitadrop.presentation.donor.DonorListIntent
import com.social.vitadrop.presentation.donor.DonorListState
import com.social.vitadrop.presentation.donor.components.DonorCard
import com.social.vitadrop.presentation.donor.viewmodel.DonorListViewModel
import kotlinx.coroutines.launch

private const val ALL = "All"
private val BLOOD_GROUP_OPTIONS = listOf(ALL, "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")
private val GENDER_OPTIONS = listOf(ALL, "Male", "Female")

private fun String.nullIfAll(): String? = if (this == ALL) null else this

// =====================================================================
// STATEFUL WRAPPER: only connects the ViewModel to the UI
// =====================================================================
@Composable
fun DonorListScreen(
    viewModel: DonorListViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    DonorListContent(
        state = state,
        onIntent = viewModel::onIntent,
        onBack = onBack
    )
}

// =====================================================================
// STATELESS UI: draws the State, sends Intents back
// =====================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DonorListContent(
    state: DonorListState,
    onIntent: (DonorListIntent) -> Unit,
    onBack: () -> Unit
) {
    // Pure UI state: is the filter sheet open?
    var showFilters by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    val visibleDonors = state.visibleDonors

    Scaffold(
        containerColor = Color(0xFFFFF5F5),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                title = {
                    Column {
                        Text(text = "Blood Donors", fontWeight = FontWeight.Bold)
                        Text(
                            text = "${visibleDonors.size} donors found",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    TextButton(onClick = { showFilters = true }) {
                        Text(text = "Filters", color = Color(0xFFD32F2F))
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

            // ================= MAIN CONTENT =================
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 90.dp
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                // SEARCH BAR
                item {
                    OutlinedTextField(
                        value = state.query,
                        onValueChange = { onIntent(DonorListIntent.Search(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search donor, city or blood group") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        },
                        shape = RoundedCornerShape(18.dp),
                        singleLine = true
                    )
                }

                // ACTIVE FILTERS
                item {
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        state.bloodGroup?.let { ActiveFilterChip(it) }
                        state.gender?.let { ActiveFilterChip(it) }
                        state.city?.let { ActiveFilterChip(it) }
                        if (state.availableOnly) ActiveFilterChip("Available")
                    }
                }

                // EMPTY STATE (only when we are NOT loading and there is NO error)
                if (!state.isLoading && state.error == null && visibleDonors.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 80.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
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

                            Text(text = "Try changing filters", color = Color.Gray)
                        }
                    }
                }

                // DONOR LIST
                items(visibleDonors, key = { it.uid }) { donor ->
                    DonorCard(donor = donor)
                }
            }

            // ================= LOADING =================
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            // ================= ERROR + RETRY =================
            val error = state.error
            if (error != null) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = error, color = Color.Red)

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { onIntent(DonorListIntent.Refresh) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                    ) {
                        Text("Retry")
                    }
                }
            }
        }

        // ================= FILTER BOTTOM SHEET =================
        if (showFilters) {
            ModalBottomSheet(
                onDismissRequest = { showFilters = false },
                sheetState = sheetState
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Text(
                        text = "Filters",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    CompactDropdownFilter(
                        label = "Blood Group",
                        selectedValue = state.bloodGroup ?: ALL,
                        options = BLOOD_GROUP_OPTIONS,
                        onValueSelected = {
                            onIntent(DonorListIntent.FilterBloodGroup(it.nullIfAll()))
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CompactDropdownFilter(
                        label = "Gender",
                        selectedValue = state.gender ?: ALL,
                        options = GENDER_OPTIONS,
                        onValueSelected = {
                            onIntent(DonorListIntent.FilterGender(it.nullIfAll()))
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CompactDropdownFilter(
                        label = "City",
                        selectedValue = state.city ?: ALL,
                        options = listOf(ALL) + state.cities,
                        onValueSelected = {
                            onIntent(DonorListIntent.FilterCity(it.nullIfAll()))
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = state.availableOnly,
                            onCheckedChange = {
                                onIntent(DonorListIntent.AvailableOnlyChanged(it))
                            }
                        )

                        Text(text = "Available donors only")
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onIntent(DonorListIntent.ClearFilters) },
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
private fun ActiveFilterChip(text: String) {
    Surface(
        shape = RoundedCornerShape(50.dp),
        color = Color(0xFFFFEBEE)
    ) {
        Text(
            text = text,
            color = Color(0xFFD32F2F),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}