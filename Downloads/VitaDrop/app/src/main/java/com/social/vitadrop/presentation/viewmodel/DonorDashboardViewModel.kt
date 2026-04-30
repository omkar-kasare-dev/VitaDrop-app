package com.social.vitadrop.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.social.vitadrop.data.repository.DashboardRepository
import com.social.vitadrop.presentation.event.DonorDashboardEvent
import com.social.vitadrop.state.DonorDashboardState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log

class DonorDashboardViewModel(
    private val repository: DashboardRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DonorDashboardState())
    val state: StateFlow<DonorDashboardState> = _state

    init {
        // 🔥 AUTO LOAD (VERY IMPORTANT FIX)
        loadDashboard()
    }

    fun onEvent(event: DonorDashboardEvent) {
        when (event) {
            is DonorDashboardEvent.LoadDashboard -> loadDashboard()
            else -> {println("------------------------")}
        }
    }

    private fun loadDashboard() {

        viewModelScope.launch {

            _state.value = _state.value.copy(isLoading = true)

            try {
                Log.d("DASHBOARD_VM", "Loading dashboard...")

                val donors = repository.getDonorCount()
                val hospitals = repository.getHospitalCount()
                val requests = repository.getRequestCount()
                val emergency = repository.getEmergencyRequests()
                val data = repository.getAllRequests()

                Log.d("DASHBOARD_VM", "Emergency size = ${emergency.size}")

                _state.value = _state.value.copy(
                    donorsCount = donors,
                    hospitalsCount = hospitals,
                    requestsCount = requests,
                    emergencyRequests = emergency,
                    requests=data,
                    isLoading = false,
                    error = null
                )

                Log.d("DASHBOARD_VM", "Dashboard updated successfully")

            } catch (e: Exception) {

                Log.e("DASHBOARD_VM", "Error: ${e.message}")

                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message,
                    emergencyRequests = emptyList()
                )
            }
        }
    }
}