package com.social.vitadrop.presentation.dashboard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.social.vitadrop.domain.usecase.GetDashboardUseCase
import com.social.vitadrop.presentation.dashboard.DonorDashboardIntent
import com.social.vitadrop.presentation.dashboard.DonorDashboardState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DonorDashboardViewModel(
    private val getDashboard: GetDashboardUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DonorDashboardState())
    val state: StateFlow<DonorDashboardState> = _state.asStateFlow()

    // Ignore a new refresh while one is already running (the screen asks on every entry).
    // There is no init { load() } on purpose: the screen sends Refresh when it appears,
    // so the data is also fresh when the user comes back (e.g. after creating a request).
    private var isRefreshing = false

    fun onIntent(intent: DonorDashboardIntent) {
        when (intent) {
            DonorDashboardIntent.Refresh -> load()
        }
    }

    private fun load() {
        if (isRefreshing) return
        isRefreshing = true

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = getDashboard()
            isRefreshing = false

            result
                .onSuccess { data ->
                    _state.update {
                        it.copy(
                            donorsCount = data.donorsCount,
                            hospitalsCount = data.hospitalsCount,
                            requestsCount = data.requestsCount,
                            emergencyRequests = data.emergencyRequests,
                            isLoading = false
                        )
                    }
                }
                .onFailure { e ->
                    // Keep the old numbers on screen and show the error with a Retry button
                    _state.update {
                        it.copy(isLoading = false, error = e.message ?: "Could not load the dashboard")
                    }
                }
        }
    }
}