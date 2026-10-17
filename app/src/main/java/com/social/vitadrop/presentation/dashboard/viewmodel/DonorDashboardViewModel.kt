package com.social.vitadrop.presentation.dashboard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.social.vitadrop.domain.usecase.GetDashboardStatsUseCase
import com.social.vitadrop.domain.usecase.ObserveEmergencyRequestsUseCase
import com.social.vitadrop.presentation.dashboard.DonorDashboardIntent
import com.social.vitadrop.presentation.dashboard.DonorDashboardState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * BEFORE: one "all or nothing" use case loaded the three counts AND the emergency list.
 * If ONE of the four calls failed or hung, the ViewModel received NOTHING, even when the other
 * calls had succeeded.
 *
 * NOW: two independent sources.
 *  - the three numbers (loaded once per refresh)
 *  - the emergency list (a live listener)
 * One failing does not blank the other. The existing screens still read the same
 * DonorDashboardState, so they need no change.
 */
class DonorDashboardViewModel(
    private val getStats: GetDashboardStatsUseCase,
    private val observeEmergencyRequests: ObserveEmergencyRequestsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DonorDashboardState())
    val state: StateFlow<DonorDashboardState> = _state.asStateFlow()

    private var emergencyJob: Job? = null
    private var isRefreshingStats = false

    // The spinner stays until BOTH sources answered once (so "No emergency requests right now"
    // is never shown just because the list has not arrived yet)
    private var statsAnswered = false
    private var emergencyAnswered = false

    private fun stillLoading() = !(statsAnswered && emergencyAnswered)

    fun onIntent(intent: DonorDashboardIntent) {
        when (intent) {
            DonorDashboardIntent.Refresh -> {
                loadStats()
                startObservingEmergency()   // does nothing if it is already running
            }
        }
    }

    private fun loadStats() {
        if (isRefreshingStats) return
        isRefreshingStats = true

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = getStats()
            isRefreshingStats = false
            statsAnswered = true

            result
                .onSuccess { stats ->
                    _state.update {
                        it.copy(
                            donorsCount = stats.donorsCount,
                            hospitalsCount = stats.hospitalsCount,
                            requestsCount = stats.requestsCount,
                            isLoading = stillLoading()
                        )
                    }
                }
                .onFailure { e ->
                    // The emergency list below stays on screen. Only the banner appears.
                    _state.update {
                        it.copy(
                            isLoading = stillLoading(),
                            error = e.message ?: "Could not load the dashboard numbers"
                        )
                    }
                }
        }
    }

    private fun startObservingEmergency() {
        if (emergencyJob?.isActive == true) return

        emergencyJob = viewModelScope.launch {
            observeEmergencyRequests()
                .catch { e ->
                    emergencyAnswered = true
                    _state.update {
                        it.copy(
                            isLoading = stillLoading(),
                            error = e.message ?: "Could not load emergency requests"
                        )
                    }
                }
                .collect { requests ->
                    emergencyAnswered = true
                    _state.update {
                        it.copy(
                            emergencyRequests = requests,
                            isLoading = stillLoading()
                        )
                    }
                }
        }
    }
}