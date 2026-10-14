package com.social.vitadrop.presentation.donor.screen
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.sp
import com.social.vitadrop.presentation.components.CompactDropdownFilter
import com.social.vitadrop.presentation.donor.DonorListIntent
import com.social.vitadrop.presentation.donor.DonorListState
import com.social.vitadrop.presentation.donor.components.DonorCard
import com.social.vitadrop.presentation.donor.viewmodel.DonorListViewModel
import kotlinx.coroutines.launch

private const val ALL = "All"
private val BLOOD_GROUP_OPTIONS = listOf(ALL, "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")
private val GENDER_OPTIONS = listOf(ALL, "Male", "Female")

// Real-World Design Palette Constants
private val AppBgColor = Color(0xFFF8FAFC)        // Slate 50 background
private val CardBgColor = Color(0xFFFFFFFF)       // Pure White Surface
private val BorderColor = Color(0xFFF1F5F9)       // Slate 100 soft border
private val TextPrimary = Color(0xFF0F172A)       // Slate 900 primary text
private val TextSecondary = Color(0xFF64748B)     // Slate 500 secondary label
private val PrimaryAccent = Color(0xFFE11D48)     // Deep Crimson/Rose
private val SoftBadgeBg = Color(0xFFFFF1F2)      // Light Rose tint
private val LightAccentBg = Color(0xFFFFE4E6)

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

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = PrimaryAccent,
        unfocusedBorderColor = BorderColor,
        focusedLabelColor = PrimaryAccent,
        unfocusedLabelColor = TextSecondary,
        focusedContainerColor = CardBgColor,
        unfocusedContainerColor = CardBgColor,
        cursorColor = PrimaryAccent
    )

    Scaffold(
        containerColor = AppBgColor,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CardBgColor),
                title = {
                    Column {
                        Text(
                            text = "Blood Donors",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${visibleDonors.size} donors found",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                actions = {
                    TextButton(onClick = { showFilters = true }) {
                        Text(
                            text = "Filters",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryAccent
                        )
                    }
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // SEARCH BAR
                item {
                    OutlinedTextField(
                        value = state.query,
                        onValueChange = { onIntent(DonorListIntent.Search(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                "Search donor, city or blood group",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = fieldColors
                    )
                }

                // ACTIVE FILTERS
                if (state.bloodGroup != null || state.gender != null || state.city != null || state.availableOnly) {
                    item {
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            state.bloodGroup?.let { ActiveFilterChip(it) }
                            state.gender?.let { ActiveFilterChip(it) }
                            state.city?.let { ActiveFilterChip(it) }
                            if (state.availableOnly) ActiveFilterChip("Available")
                        }
                    }
                }

                // EMPTY STATE (only when we are NOT loading and there is NO error)
                if (!state.isLoading && state.error == null && visibleDonors.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 60.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = SoftBadgeBg,
                                modifier = Modifier.size(64.dp),
                                border = BorderStroke(1.dp, LightAccentBg)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                        tint = PrimaryAccent,
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "No donors found",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Try changing filters",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
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
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = PrimaryAccent,
                    strokeWidth = 2.dp
                )
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
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                        border = BorderStroke(1.dp, Color(0xFFFCA5A5))
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(20.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = error,
                                color = PrimaryAccent,
                                fontSize = 12.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { onIntent(DonorListIntent.Refresh) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent)
                            ) {
                                Text("Retry", fontSize = 12.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // ================= FILTER BOTTOM SHEET =================
        if (showFilters) {
            ModalBottomSheet(
                onDismissRequest = { showFilters = false },
                sheetState = sheetState,
                //containerColor = CardBgCardColor
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Text(
                        text = "Filters",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CompactDropdownFilter(
                        label = "Blood Group",
                        selectedValue = state.bloodGroup ?: ALL,
                        options = BLOOD_GROUP_OPTIONS,
                        onValueSelected = {
                            onIntent(DonorListIntent.FilterBloodGroup(it.nullIfAll()))
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    CompactDropdownFilter(
                        label = "Gender",
                        selectedValue = state.gender ?: ALL,
                        options = GENDER_OPTIONS,
                        onValueSelected = {
                            onIntent(DonorListIntent.FilterGender(it.nullIfAll()))
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    CompactDropdownFilter(
                        label = "City",
                        selectedValue = state.city ?: ALL,
                        options = listOf(ALL) + state.cities,
                        onValueSelected = {
                            onIntent(DonorListIntent.FilterCity(it.nullIfAll()))
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        onClick = { onIntent(DonorListIntent.AvailableOnlyChanged(!state.availableOnly)) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (state.availableOnly) SoftBadgeBg else AppBgColor,
                        border = BorderStroke(
                            1.dp,
                            if (state.availableOnly) LightAccentBg else BorderColor
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Checkbox(
                                checked = state.availableOnly,
                                onCheckedChange = {
                                    onIntent(DonorListIntent.AvailableOnlyChanged(it))
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = PrimaryAccent,
                                    uncheckedColor = TextSecondary
                                )
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Text(
                                text = "Available donors only",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onIntent(DonorListIntent.ClearFilters) },
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = TextPrimary
                            )
                        ) {
                            Text("Clear", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }

                        Button(
                            onClick = {
                                scope.launch {
                                    sheetState.hide()
                                    showFilters = false
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryAccent
                            )
                        ) {
                            Text("Apply", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

// ================= ACTIVE FILTER CHIP =================

@Composable
private fun ActiveFilterChip(text: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = SoftBadgeBg,
        border = BorderStroke(1.dp, LightAccentBg)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryAccent,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

// Local helper extension for progress bar modifier size syntax compatibility
private fun Modifier.modifier_size(size: androidx.compose.ui.unit.Dp) = this.size(size)