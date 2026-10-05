package com.social.vitadrop.presentation.request.viewmodel



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.social.vitadrop.domain.usecase.GetRequestByIdUseCase
import com.social.vitadrop.presentation.request.details.RequestDetailsIntent
import com.social.vitadrop.presentation.request.details.RequestDetailsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RequestDetailsViewModel(
    private val requestId: String,
    private val getRequestById: GetRequestByIdUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RequestDetailsState())
    val state: StateFlow<RequestDetailsState> = _state.asStateFlow()

    // Load once when the ViewModel is created (no more LaunchedEffect reload on every rotation)
    init {
        load()
    }

    fun onIntent(intent: RequestDetailsIntent) {
        when (intent) {
            RequestDetailsIntent.Retry -> load()
        }
    }

    private fun load() {
        if (requestId.isBlank()) {
            _state.update { it.copy(isLoading = false, errorMessage = "Invalid request.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            // Before: an exception here crashed the app, and "not found" showed a spinner forever.
            getRequestById(requestId)
                .onSuccess { request ->
                    _state.update { it.copy(request = request, isLoading = false) }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(isLoading = false, errorMessage = e.message ?: "Could not load the request.")
                    }
                }
        }
    }
}