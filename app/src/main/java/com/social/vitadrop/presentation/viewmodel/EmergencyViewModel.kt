package com.social.vitadrop.presentation.viewmodel
/*
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.google.firebase.auth.FirebaseAuth

import com.social.vitadrop.domain.usecase.HasRespondedUseCase
import com.social.vitadrop.domain.usecase.ObserveResponseCountUseCase
import com.social.vitadrop.domain.usecase.RespondToRequestUseCase
import com.social.vitadrop.presentation.event.EmergencyEvent
import com.social.vitadrop.state.EmergencyUiState

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EmergencyViewModel(

    private val respondToRequestUseCase:
    RespondToRequestUseCase,

    private val hasRespondedUseCase:
    HasRespondedUseCase,

    private val observeResponseCountUseCase:
    ObserveResponseCountUseCase

) : ViewModel() {

    private val _uiState =
        MutableStateFlow(EmergencyUiState())

    val uiState: StateFlow<EmergencyUiState> =
        _uiState.asStateFlow()

    private val currentUserId: String
        get() = FirebaseAuth
            .getInstance()
            .currentUser
            ?.uid
            ?: ""

    fun onEvent(
        event: EmergencyEvent
    ) {

        when (event) {

            is EmergencyEvent.RespondToRequest -> {

                respondToRequest(
                    event.requestId
                )
            }

            is EmergencyEvent.CheckAlreadyResponded -> {

                checkAlreadyResponded(
                    event.requestId
                )
            }

            is EmergencyEvent.ObserveResponseCount -> {

                observeResponseCount(
                    event.requestId
                )
            }
        }
    }

    private fun respondToRequest(
        requestId: String
    ) {

        viewModelScope.launch {

            try {

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = true
                    )

                val alreadyResponded =
                    hasRespondedUseCase(
                        requestId,
                        currentUserId
                    )

                if (alreadyResponded) {

                    val updatedMap =
                        _uiState.value.respondedRequests
                            .toMutableMap()

                    updatedMap[requestId] = true

                    _uiState.value =
                        _uiState.value.copy(
                            respondedRequests =
                                updatedMap,
                            isLoading = false
                        )

                    return@launch
                }

                //
                Log.d(
                    "EmergencyVM",
                    "Current UID = $currentUserId"
                )
                //
                respondToRequestUseCase(
                    requestId,
                    currentUserId
                )

                val updatedMap =
                    _uiState.value.respondedRequests
                        .toMutableMap()

                updatedMap[requestId] = true

                _uiState.value =
                    _uiState.value.copy(
                        respondedRequests =
                            updatedMap,
                        isLoading = false
                    )

            } catch (e: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        error = e.message,
                        isLoading = false
                    )
            }
        }
    }

    private fun checkAlreadyResponded(
        requestId: String
    ) {

        viewModelScope.launch {

            try {

                val result =
                    hasRespondedUseCase(
                        requestId,
                        currentUserId
                    )

                val updatedMap =
                    _uiState.value.respondedRequests
                        .toMutableMap()

                updatedMap[requestId] = result

                _uiState.value =
                    _uiState.value.copy(
                        respondedRequests =
                            updatedMap
                    )

            } catch (_: Exception) {
            }
        }
    }

    private fun observeResponseCount(
        requestId: String
    ) {

        viewModelScope.launch {

            observeResponseCountUseCase(
                requestId
            ).collectLatest { count ->

                val updatedCounts =
                    _uiState.value.responseCounts
                        .toMutableMap()

                updatedCounts[requestId] =
                    count

                _uiState.value =
                    _uiState.value.copy(
                        responseCounts =
                            updatedCounts
                    )
            }
        }
    }
}

 */



import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.google.firebase.auth.FirebaseAuth

import com.social.vitadrop.domain.repository.ResponseRepository
import com.social.vitadrop.presentation.event.EmergencyEvent
import com.social.vitadrop.state.EmergencyUiState

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EmergencyViewModel(

    private val repository:
    ResponseRepository

) : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            EmergencyUiState()
        )

    val uiState: StateFlow<EmergencyUiState> =
        _uiState.asStateFlow()

    private val currentUserId: String?
        get() = FirebaseAuth
            .getInstance()
            .currentUser
            ?.uid

    fun onEvent(
        event: EmergencyEvent
    ) {

        when (event) {

            is EmergencyEvent.RespondToRequest -> {

                respondToRequest(
                    event.requestId
                )
            }

            is EmergencyEvent.CheckAlreadyResponded -> {

                checkAlreadyResponded(
                    event.requestId
                )
            }

            is EmergencyEvent.ObserveResponseCount -> {

                observeResponseCount(
                    event.requestId
                )
            }
        }
    }

    private fun respondToRequest(
        requestId: String
    ) {

        viewModelScope.launch {

            try {

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = true,
                        error = null
                    )

                val donorId =
                    currentUserId

                if (donorId == null) {

                    _uiState.value =
                        _uiState.value.copy(
                            error =
                                "User not logged in",
                            isLoading = false
                        )

                    return@launch
                }

                Log.d(
                    "EmergencyVM",
                    "Current UID = $donorId"
                )

                val alreadyResponded =
                    repository.hasAlreadyResponded(
                        requestId,
                        donorId
                    )

                if (alreadyResponded) {

                    val updatedMap =
                        _uiState.value
                            .respondedRequests
                            .toMutableMap()

                    updatedMap[requestId] =
                        true

                    _uiState.value =
                        _uiState.value.copy(
                            respondedRequests =
                                updatedMap,
                            isLoading = false
                        )

                    return@launch
                }
//
                Log.d(
                    "EmergencyVM",
                    "Respond Clicked"
                )

                Log.d(
                    "EmergencyVM",
                    "RequestId = $requestId"
                )

                Log.d(
                    "EmergencyVM",
                    "DonorId = $donorId"
                )


                repository.respondToRequest(
                    requestId,
                    donorId
                )

                Log.d(
                    "EmergencyVM",
                    "Response Saved Successfully"
                )

                val updatedMap =
                    _uiState.value
                        .respondedRequests
                        .toMutableMap()

                updatedMap[requestId] =
                    true

                _uiState.value =
                    _uiState.value.copy(
                        respondedRequests =
                            updatedMap,
                        isLoading = false
                    )

            } catch (e: Exception) {

                Log.e(
                    "EmergencyVM",
                    "Respond Error",
                    e
                )

                _uiState.value =
                    _uiState.value.copy(
                        error =
                            e.message,
                        isLoading = false
                    )
            }
        }
    }

    private fun checkAlreadyResponded(
        requestId: String
    ) {

        viewModelScope.launch {

            try {

                val donorId =
                    currentUserId
                        ?: return@launch

                val result =
                    repository.hasAlreadyResponded(
                        requestId,
                        donorId
                    )

                val updatedMap =
                    _uiState.value
                        .respondedRequests
                        .toMutableMap()

                updatedMap[requestId] =
                    result

                _uiState.value =
                    _uiState.value.copy(
                        respondedRequests =
                            updatedMap
                    )

            } catch (e: Exception) {

                Log.e(
                    "EmergencyVM",
                    "Check Response Error",
                    e
                )
            }
        }
    }

    private fun observeResponseCount(
        requestId: String
    ) {

        Log.d(
            "EmergencyVM",
            "Observing Count: $requestId"
        )
        viewModelScope.launch {

            repository
                .observeResponseCount(
                    requestId
                )
                .collectLatest { count ->
                    Log.d(
                        "EmergencyVM",
                        "Count = $count"
                    )

                    val updatedCounts =
                        _uiState.value
                            .responseCounts
                            .toMutableMap()

                    updatedCounts[requestId] =
                        count

                    _uiState.value =
                        _uiState.value.copy(
                            responseCounts =
                                updatedCounts
                        )
                }
        }
    }
}