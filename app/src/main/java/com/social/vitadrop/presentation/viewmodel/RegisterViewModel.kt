package com.social.vitadrop.presentation.viewmodel

/*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.social.vitadrop.domain.model.User
import com.social.vitadrop.domain.usecase.RegisterUserUseCase
import com.social.vitadrop.presentation.event.RegisterEvent
import com.social.vitadrop.state.RegisterState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerUserUseCase: RegisterUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state

    fun onEvent(event: RegisterEvent) {
        when (event) {

            is RegisterEvent.UpdateField -> {
                _state.value = _state.value.copy(
                    name = event.name ?: _state.value.name,
                    email = event.email ?: _state.value.email,
                    phone = event.phone ?: _state.value.phone,
                    bloodGroup = event.bloodGroup ?: _state.value.bloodGroup,
                    city = event.city ?: _state.value.city,
                    address = event.address ?: _state.value.address,
                    password = event.password ?: _state.value.password,
                    role = event.role ?: _state.value.role
                )
            }

            RegisterEvent.Register -> register()
        }
    }

    private fun register() {
        val s = _state.value

        if (s.role.isEmpty()) {
            _state.value = s.copy(message = "Select role")
            return
        }

        if (s.email.isBlank() || s.password.isBlank()) {
            _state.value = s.copy(message = "Email & Password required")
            return
        }

        viewModelScope.launch {
            _state.value = s.copy(isLoading = true)

            val result = registerUserUseCase(
                User(
                    name = s.name,
                    email = s.email,
                    phone = s.phone,
                    role = s.role,
                    bloodGroup = s.bloodGroup,
                    city = s.city,
                    address = s.address
                ),
                s.password
            )

            _state.value = if (result.isSuccess) {
                s.copy(isLoading = false, message = "Registration Successful")
            } else {
                s.copy(isLoading = false, message = result.exceptionOrNull()?.message ?: "Registration Failed")
            }
        }
    }
}

 */



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.social.vitadrop.domain.model.User
import com.social.vitadrop.domain.usecase.RegisterUserUseCase
import com.social.vitadrop.presentation.event.RegisterEvent
import com.social.vitadrop.state.RegisterState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerUserUseCase: RegisterUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state

    fun onEvent(event: RegisterEvent) {

        when (event) {

            is RegisterEvent.UpdateField -> {

                _state.value = _state.value.copy(

                    // Basic Details
                    uid = event.uid ?: _state.value.uid,
                    fullName = event.fullName ?: _state.value.fullName,
                    email = event.email ?: _state.value.email,
                    phone = event.phone ?: _state.value.phone,
                    gender = event.gender ?: _state.value.gender,
                    age = event.age ?: _state.value.age,

                    //hospotal:
                    licenseNumber = event.licenseNumber?:_state.value.licenseNumber,

                    // Blood Details
                    bloodGroup = event.bloodGroup ?: _state.value.bloodGroup,

                    // Address
                    city = event.city ?: _state.value.city,
                    state = event.state ?: _state.value.state,
                    address = event.address ?: _state.value.address,

                    // Location
                    latitude = event.latitude ?: _state.value.latitude,
                    longitude = event.longitude ?: _state.value.longitude,

                    // Profile
                    profileImage = event.profileImage ?: _state.value.profileImage,
                    weight = event.weight ?: _state.value.weight,

                    // Authentication
                    password = event.password ?: _state.value.password,
                    role = event.role ?: _state.value.role,

                    // Status
                    isAvailable = event.isAvailable ?: _state.value.isAvailable,
                    isVerified = event.isVerified ?: _state.value.isVerified,
                    isBlocked = event.isBlocked ?: _state.value.isBlocked,

                    // Device
                    devicePlatform = event.devicePlatform ?: _state.value.devicePlatform,
                    fcmToken = event.fcmToken ?: _state.value.fcmToken
                )
            }

            RegisterEvent.Register -> {
                register()
            }
        }
    }

    private fun register() {

        val s = _state.value

        // Validation
        if (s.role.isBlank()) {
            _state.value = s.copy(message = "Please select role")
            return
        }

        if (s.email.isBlank() || s.password.isBlank()) {
            _state.value = s.copy(message = "Email & Password required")
            return
        }

        if (s.fullName.isBlank()) {
            _state.value = s.copy(message = "Full name required")
            return
        }

        viewModelScope.launch {

            _state.value = s.copy(isLoading = true)

            val user = User(

                // Basic Details
                uid = s.uid,
                fullName = s.fullName,
                email = s.email,
                phone = s.phone,
                gender = s.gender,
                age = s.age.toIntOrNull() ?: 0,
                // hospital
                licenseNumber = s.licenseNumber,

                // Role
                role = s.role,

                // Blood Details
                bloodGroup = s.bloodGroup,

                // Address
                city = s.city,
                state = s.state,
                address = s.address,

                // Location
                latitude = s.latitude.toDoubleOrNull() ?: 0.0,
                longitude = s.longitude.toDoubleOrNull() ?: 0.0,

                // Profile
                profileImage = s.profileImage,
                weight = s.weight.toDoubleOrNull() ?: 0.0,

                // Donation
                lastDonationDate = null,

                // Status
                isAvailable = s.isAvailable,
                isVerified = s.isVerified,
                isBlocked = s.isBlocked,

                // Device
                devicePlatform = s.devicePlatform,
                fcmToken = s.fcmToken,

                // Timestamps
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now(),
                lastActive = Timestamp.now()
            )

            val result = registerUserUseCase(
                user,
                s.password
            )

            _state.value = if (result.isSuccess) {

                s.copy(
                    isLoading = false,
                    message = "Registration Successful"
                )

            } else {

                s.copy(
                    isLoading = false,
                    message = result.exceptionOrNull()?.message
                        ?: "Registration Failed"
                )
            }
        }
    }
}