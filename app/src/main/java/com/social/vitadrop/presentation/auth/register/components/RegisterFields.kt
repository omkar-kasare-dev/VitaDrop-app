package com.social.vitadrop.presentation.auth.register.components
/*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.social.vitadrop.presentation.auth.register.RegisterField
import com.social.vitadrop.presentation.auth.register.RegisterIntent
import com.social.vitadrop.presentation.auth.register.RegisterState
import com.social.vitadrop.presentation.auth.register.screen.ModernTextField


@Composable
fun DonorFields(state: RegisterState, onIntent: (RegisterIntent) -> Unit) {
    fun change(field: RegisterField): (String) -> Unit =
        { onIntent(RegisterIntent.FieldChanged(field, it)) }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        ModernTextField(state.fullName, change(RegisterField.FULL_NAME), "Full Name",
            icon = { Icon(Icons.Default.Person, contentDescription = null) })
        ModernTextField(state.phone, change(RegisterField.PHONE), "Phone Number",
            keyboardType = KeyboardType.Phone)
        ModernTextField(state.gender, change(RegisterField.GENDER), "Gender")
        ModernTextField(state.age, change(RegisterField.AGE), "Age",
            keyboardType = KeyboardType.Number)
        ModernTextField(state.bloodGroup, change(RegisterField.BLOOD_GROUP), "Blood Group")
        ModernTextField(state.weight, change(RegisterField.WEIGHT), "Weight (KG)",
            keyboardType = KeyboardType.Decimal)
        ModernTextField(state.city, change(RegisterField.CITY), "City",
            icon = { Icon(Icons.Default.LocationOn, contentDescription = null) })
        ModernTextField(state.state, change(RegisterField.STATE), "State")
        ModernTextField(state.address, change(RegisterField.ADDRESS), "Address")
        Text("Lat : ${state.latitude}\nLng : ${state.longitude}")
    }
}

@Composable
fun HospitalFields(state: RegisterState, onIntent: (RegisterIntent) -> Unit) {
    fun change(field: RegisterField): (String) -> Unit =
        { onIntent(RegisterIntent.FieldChanged(field, it)) }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        ModernTextField(state.fullName, change(RegisterField.FULL_NAME), "Hospital Name")
        ModernTextField(state.phone, change(RegisterField.PHONE), "Contact Number",
            keyboardType = KeyboardType.Phone)
        ModernTextField(state.licenseNumber, change(RegisterField.LICENSE), "License Number")
        ModernTextField(state.city, change(RegisterField.CITY), "City")
        ModernTextField(state.state, change(RegisterField.STATE), "State")
        ModernTextField(state.address, change(RegisterField.ADDRESS), "Address")
    }
}

 */


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.social.vitadrop.domain.model.BloodGroups
import com.social.vitadrop.presentation.auth.register.RegisterField
import com.social.vitadrop.presentation.auth.register.RegisterIntent
import com.social.vitadrop.presentation.auth.register.RegisterState
import com.social.vitadrop.presentation.auth.register.screen.ModernTextField
import com.social.vitadrop.presentation.components.CompactDropdownFilter


private val GENDER_OPTIONS = listOf("Male", "Female", "Other")

@Composable
fun DonorFields(state: RegisterState, onIntent: (RegisterIntent) -> Unit) {
    fun change(field: RegisterField): (String) -> Unit =
        { onIntent(RegisterIntent.FieldChanged(field, it)) }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        ModernTextField(state.fullName, change(RegisterField.FULL_NAME), "Full Name",
            icon = { Icon(Icons.Default.Person, contentDescription = null) })
        ModernTextField(state.phone, change(RegisterField.PHONE), "Phone Number",
            keyboardType = KeyboardType.Phone)

        // Dropdowns instead of free text: the alert server compares these values EXACTLY
        CompactDropdownFilter(
            label = "Gender",
            selectedValue = state.gender.ifBlank { "Select gender" },
            options = GENDER_OPTIONS,
            onValueSelected = change(RegisterField.GENDER)
        )

        ModernTextField(state.age, change(RegisterField.AGE), "Age",
            keyboardType = KeyboardType.Number)

        CompactDropdownFilter(
            label = "Blood Group",
            selectedValue = state.bloodGroup.ifBlank { "Select blood group" },
            options = BloodGroups.ALL,
            onValueSelected = change(RegisterField.BLOOD_GROUP)
        )

        ModernTextField(state.weight, change(RegisterField.WEIGHT), "Weight (KG)",
            keyboardType = KeyboardType.Decimal)
        ModernTextField(state.city, change(RegisterField.CITY), "City",
            icon = { Icon(Icons.Default.LocationOn, contentDescription = null) })
        ModernTextField(state.state, change(RegisterField.STATE), "State")
        ModernTextField(state.address, change(RegisterField.ADDRESS), "Address")

        // Donors without a location can never be alerted, so show the status clearly
        Text(
            if (state.latitude.isNotBlank()) {
                "📍 Location detected:"
            // "📍 Location detected: ${state.latitude}, ${state.longitude}"
            } else {
                "⚠ Location not detected yet. Turn on GPS and allow location access."
            }
        )
    }
}

@Composable
fun HospitalFields(state: RegisterState, onIntent: (RegisterIntent) -> Unit) {
    fun change(field: RegisterField): (String) -> Unit =
        { onIntent(RegisterIntent.FieldChanged(field, it)) }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        ModernTextField(state.fullName, change(RegisterField.FULL_NAME), "Hospital Name")
        ModernTextField(state.phone, change(RegisterField.PHONE), "Contact Number",
            keyboardType = KeyboardType.Phone)
        ModernTextField(state.licenseNumber, change(RegisterField.LICENSE), "License Number")
        ModernTextField(state.city, change(RegisterField.CITY), "City")
        ModernTextField(state.state, change(RegisterField.STATE), "State")
        ModernTextField(state.address, change(RegisterField.ADDRESS), "Address")
    }
}