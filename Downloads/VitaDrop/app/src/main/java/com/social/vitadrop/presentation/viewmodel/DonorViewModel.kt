package com.social.vitadrop.presentation.viewmodel



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.social.vitadrop.domain.repository.DonorRepository
import com.social.vitadrop.presentation.event.DonorEvent
import com.social.vitadrop.state.DonorState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DonorViewModel(
    private val repository: DonorRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DonorState())
    val state: StateFlow<DonorState> = _state

    fun onEvent(event: DonorEvent) {
        when (event) {
            is DonorEvent.LoadDonors -> loadDonors()
        }
    }

    private fun loadDonors() {

        viewModelScope.launch {

            _state.value = _state.value.copy(isLoading = true)

            try {

                val donors = repository.getAllDonors()

                _state.value = _state.value.copy(
                    donors = donors,
                    isLoading = false
                )

            } catch (e: Exception) {

                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
}