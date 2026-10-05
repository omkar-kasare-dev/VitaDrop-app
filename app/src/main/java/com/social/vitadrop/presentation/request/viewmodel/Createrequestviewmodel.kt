package com.social.vitadrop.presentation.request.viewmodel



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.social.vitadrop.domain.model.NewRequest
import com.social.vitadrop.domain.usecase.CreateRequestUseCase
import com.social.vitadrop.presentation.request.CreateRequestEffect
import com.social.vitadrop.presentation.request.CreateRequestIntent
import com.social.vitadrop.presentation.request.CreateRequestState
import com.social.vitadrop.presentation.request.RequestField
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateRequestViewModel(
    private val createRequest: CreateRequestUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CreateRequestState())
    val state: StateFlow<CreateRequestState> = _state.asStateFlow()

    private val _effect = Channel<CreateRequestEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: CreateRequestIntent) {
        when (intent) {
            is CreateRequestIntent.FieldChanged ->
                _state.update { it.withField(intent.field, intent.value).copy(errorMessage = null) }

            is CreateRequestIntent.UrgencyChanged ->
                _state.update { it.copy(urgency = intent.value) }

            is CreateRequestIntent.EmergencyToggled ->
                _state.update { it.copy(isEmergency = intent.value) }

            is CreateRequestIntent.LocationReceived ->
                _state.update {
                    it.copy(latitude = intent.lat.toString(), longitude = intent.lng.toString())
                }

            CreateRequestIntent.Submit -> submit()
        }
    }

    private fun submit() {
        val s = _state.value
        if (s.isLoading) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            // Validation lives in the use case (business rules), the UI only shows the message.
            val result = createRequest(s.toNewRequest())

            _state.update { it.copy(isLoading = false) }

            result
                .onSuccess { _effect.send(CreateRequestEffect.RequestCreated) }
                .onFailure { e ->
                    _state.update { it.copy(errorMessage = e.message ?: "Could not send the request") }
                }
        }
    }
}

private fun CreateRequestState.withField(field: RequestField, value: String) = when (field) {
    RequestField.PATIENT_NAME -> copy(patientName = value)
    RequestField.BLOOD_GROUP -> copy(bloodGroup = value)
    RequestField.UNITS -> copy(unitsRequired = value)
    RequestField.CONTACT_PERSON -> copy(contactPerson = value)
    RequestField.CONTACT_NUMBER -> copy(contactNumber = value)
    RequestField.CITY -> copy(city = value)
    RequestField.DESCRIPTION -> copy(description = value)
}

private fun CreateRequestState.toNewRequest() = NewRequest(
    patientName = patientName,
    bloodGroup = bloodGroup,
    unitsRequired = unitsRequired.toIntOrNull() ?: 0,
    contactPerson = contactPerson,
    contactPhone = contactNumber,
    city = city,
    description = description,
    urgency = urgency,
    isEmergency = isEmergency,
    latitude = latitude.toDoubleOrNull() ?: 0.0,
    longitude = longitude.toDoubleOrNull() ?: 0.0
)