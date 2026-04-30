package com.social.vitadrop.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.social.vitadrop.domain.model.RequestModel
import com.social.vitadrop.domain.repository.RequestRepository
import com.social.vitadrop.presentation.event.RequestEvent
import com.social.vitadrop.state.RequestState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RequestViewModel(
    private val repository: RequestRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RequestState())
    val state: StateFlow<RequestState> = _state

    fun onEvent(event: RequestEvent) {
        when (event) {

            is RequestEvent.OnPatientNameChange ->
                _state.value = _state.value.copy(patientName = event.value)

            is RequestEvent.OnBloodGroupChange ->
                _state.value = _state.value.copy(bloodGroup = event.value)

            is RequestEvent.OnHospitalNameChange ->{
                _state.value = _state.value.copy(hospitalName = event.value)
                println("Hospital: ${state.value.hospitalName}")
            }


            is RequestEvent.OnCityChange ->
                _state.value = _state.value.copy(city = event.value)

            is RequestEvent.OnContactNumberChange ->{
                _state.value = _state.value.copy(contactNumber = event.value)
                println("Contact: ${state.value.contactNumber}")
            }

            is RequestEvent.OnEmergencyToggle ->
                _state.value = _state.value.copy(isEmergency = event.value)

            is RequestEvent.OnUnitsChange ->
                _state.value = _state.value.copy(unitsRequired = event.value)

            is RequestEvent.OnRequestTypeChange ->
                _state.value = _state.value.copy(requestType = event.value)

            is RequestEvent.SubmitRequest -> submitRequest()

            else -> {
                println("----------------------")
            }
        }
    }

    private fun submitRequest() {

        if (_state.value.isLoading) return  // prevent double click

        viewModelScope.launch {

            try {
                _state.value = _state.value.copy(
                    isLoading = true,
                    isSuccess = false
                )

                val request = RequestModel(
                    patientName = state.value.patientName,
                    bloodGroup = state.value.bloodGroup,
                    unitsRequired = state.value.unitsRequired,
                    hospitalName = state.value.hospitalName,
                    city = state.value.city,
                    contactNumber = state.value.contactNumber,
                    requestType = state.value.requestType,
                    isEmergency = state.value.isEmergency,
                    createdBy = "" // repo fills auth id
                )

                repository.createRequest(request)

                _state.value = _state.value.copy(
                    isLoading = false,
                    isSuccess = true,

                    // reset form after success
                    patientName = "",
                    bloodGroup = "",
                    unitsRequired = "",
                    hospitalName = "",
                    city = "",
                    contactNumber = "",
                    isEmergency = false,
                    requestType = "blood"
                )

            } catch (e: Exception) {

                _state.value = _state.value.copy(
                    isLoading = false,
                    isSuccess = false
                )
            }
        }
    }




    //

}