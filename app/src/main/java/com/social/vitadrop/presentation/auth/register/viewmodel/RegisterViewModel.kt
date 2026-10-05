package com.social.vitadrop.presentation.auth.register.viewmodel



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.social.vitadrop.domain.model.User
import com.social.vitadrop.domain.model.UserRole
import com.social.vitadrop.domain.usecase.RegisterUserUseCase
import com.social.vitadrop.presentation.auth.register.RegisterEffect
import com.social.vitadrop.presentation.auth.register.RegisterField
import com.social.vitadrop.presentation.auth.register.RegisterIntent
import com.social.vitadrop.presentation.auth.register.RegisterState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerUser: RegisterUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    private val _effect = Channel<RegisterEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: RegisterIntent) {
        when (intent) {
            is RegisterIntent.SelectRole ->
                _state.update { it.copy(role = intent.role, errorMessage = null) }

            is RegisterIntent.FieldChanged ->
                _state.update { it.withField(intent.field, intent.value) }

            is RegisterIntent.LocationReceived ->
                _state.update {
                    it.copy(latitude = intent.lat.toString(), longitude = intent.lng.toString())
                }

            RegisterIntent.Submit -> submit()
        }
    }

    private fun submit() {
        val s = _state.value
        if (s.isLoading) return

        val role = s.role ?: return showError("Please select role")
        if (s.email.isBlank() || s.password.isBlank()) return showError("Email & password required")
        if (s.password.length < 6) return showError("Password must be at least 6 characters")
        if (s.fullName.isBlank()) return showError("Name required")
        if (role == UserRole.HOSPITAL && s.licenseNumber.isBlank()) return showError("License number required")

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val result = registerUser(s.toDomain(role), s.password)

            _state.update { it.copy(isLoading = false) }
            result
                .onSuccess { _effect.send(RegisterEffect.RegistrationSuccess) }
                .onFailure { e ->
                    _state.update { it.copy(errorMessage = e.message ?: "Registration failed") }
                }
        }
    }

    private fun showError(message: String) =
        _state.update { it.copy(errorMessage = message) }
}

private fun RegisterState.withField(field: RegisterField, value: String) = when (field) {
    RegisterField.EMAIL -> copy(email = value)
    RegisterField.PASSWORD -> copy(password = value)
    RegisterField.FULL_NAME -> copy(fullName = value)
    RegisterField.PHONE -> copy(phone = value)
    RegisterField.GENDER -> copy(gender = value)
    RegisterField.AGE -> copy(age = value)
    RegisterField.BLOOD_GROUP -> copy(bloodGroup = value)
    RegisterField.WEIGHT -> copy(weight = value)
    RegisterField.LICENSE -> copy(licenseNumber = value)
    RegisterField.CITY -> copy(city = value)
    RegisterField.STATE -> copy(state = value)
    RegisterField.ADDRESS -> copy(address = value)
}

private fun RegisterState.toDomain(role: UserRole) = User(
    fullName = fullName,
    email = email,
    role = role,
    phone = phone,
    gender = gender,
    age = age.toIntOrNull() ?: 0,
    bloodGroup = bloodGroup,
    weight = weight.toDoubleOrNull() ?: 0.0,
    city = city,
    state = state,
    address = address,
    latitude = latitude.toDoubleOrNull() ?: 0.0,
    longitude = longitude.toDoubleOrNull() ?: 0.0,
    licenseNumber = licenseNumber
)