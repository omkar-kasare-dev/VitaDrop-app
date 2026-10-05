package com.social.vitadrop.presentation.emergency.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.social.vitadrop.domain.usecase.HasAlreadyRespondedUseCase
import com.social.vitadrop.domain.usecase.ObserveResponseCountUseCase
import com.social.vitadrop.domain.usecase.RespondToRequestUseCase
import com.social.vitadrop.presentation.emergency.EmergencyEffect
import com.social.vitadrop.presentation.emergency.EmergencyIntent
import com.social.vitadrop.presentation.emergency.EmergencyState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EmergencyViewModel(
    private val respondToRequest: RespondToRequestUseCase,
    private val hasAlreadyResponded: HasAlreadyRespondedUseCase,
    private val observeResponseCount: ObserveResponseCountUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(EmergencyState())
    val state: StateFlow<EmergencyState> = _state.asStateFlow()

    private val _effect = Channel<EmergencyEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    // Requests whose live count is already being observed.
    // Without this, every re-composition / rotation would add one more Firestore listener.
    private val observedRequestIds = mutableSetOf<String>()

    fun onIntent(intent: EmergencyIntent) {
        when (intent) {
            is EmergencyIntent.Respond -> respond(intent.requestId)
            is EmergencyIntent.RequestShown -> onRequestShown(intent.requestId)
        }
    }

    // ---------------------------------------------------------------
    // A card became visible: load its status and start its live count
    // ---------------------------------------------------------------
    private fun onRequestShown(requestId: String) {

        // 1) has the current donor already responded? (retried until it succeeds once)
        if (requestId !in _state.value.respondedRequests) {
            viewModelScope.launch {
                hasAlreadyResponded(requestId)
                    .onSuccess { responded ->
                        _state.update {
                            it.copy(respondedRequests = it.respondedRequests + (requestId to responded))
                        }
                    }
                    .onFailure { Log.e(TAG, "Could not check response status", it) }
            }
        }

        // 2) live number of responses (started only once per request)
        if (observedRequestIds.add(requestId)) {
            viewModelScope.launch {
                observeResponseCount(requestId)
                    .catch { e ->
                        Log.e(TAG, "Response count listener failed", e)
                        observedRequestIds.remove(requestId)   // allow a retry next time the card is shown
                    }
                    .collect { count ->
                        _state.update {
                            it.copy(responseCounts = it.responseCounts + (requestId to count))
                        }
                    }
            }
        }
    }

    // ---------------------------------------------------------------
    // The donor tapped "Respond"
    // ---------------------------------------------------------------
    private fun respond(requestId: String) {
        if (requestId in _state.value.respondingRequestIds) return   // ignore double taps

        viewModelScope.launch {
            _state.update { it.copy(respondingRequestIds = it.respondingRequestIds + requestId) }

            val result = respondToRequest(requestId)

            _state.update { it.copy(respondingRequestIds = it.respondingRequestIds - requestId) }

            result
                .onSuccess {
                    _state.update {
                        it.copy(respondedRequests = it.respondedRequests + (requestId to true))
                    }
                    _effect.send(EmergencyEffect.ShowMessage("Response sent. Thank you for helping!"))
                }
                .onFailure { e ->
                    Log.e(TAG, "Respond failed", e)
                    _effect.send(
                        EmergencyEffect.ShowMessage(e.message ?: "Could not send your response")
                    )
                }
        }
    }

    private companion object {
        const val TAG = "EmergencyVM"
    }
}