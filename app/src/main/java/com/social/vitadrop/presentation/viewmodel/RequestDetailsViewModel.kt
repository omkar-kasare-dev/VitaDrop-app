package com.social.vitadrop.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.social.vitadrop.domain.model.RequestModel
import com.social.vitadrop.domain.repository.ResponseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RequestDetailsViewModel(
    private val repository: ResponseRepository
) : ViewModel() {

    private val _request =
        MutableStateFlow<RequestModel?>(null)

    val request: StateFlow<RequestModel?> =
        _request.asStateFlow()

    fun loadRequest(
        requestId: String
    ) {

        viewModelScope.launch {

            _request.value =
                repository.getRequestById(
                    requestId
                )
        }
    }
}