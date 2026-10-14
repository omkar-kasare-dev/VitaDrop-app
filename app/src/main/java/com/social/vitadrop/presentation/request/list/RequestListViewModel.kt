package com.social.vitadrop.presentation.request.list


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.social.vitadrop.domain.usecase.GetOpenRequestsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RequestListViewModel(
    private val getOpenRequests: GetOpenRequestsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RequestListState())
    val state: StateFlow<RequestListState> = _state.asStateFlow()

    private var isRefreshing = false

    // Load once when the ViewModel is created (this screen is a leaf: no reload needed on return)
    init {
        load()
    }

    fun onIntent(intent: RequestListIntent) {
        when (intent) {
            RequestListIntent.Refresh -> load()
        }
    }

    private fun load() {
        if (isRefreshing) return
        isRefreshing = true

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = getOpenRequests()
            isRefreshing = false

            result
                .onSuccess { requests ->
                    _state.update { it.copy(requests = requests, isLoading = false) }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(isLoading = false, error = e.message ?: "Could not load requests")
                    }
                }
        }
    }
}