package com.social.vitadrop.presentation.emergency.viewmodel
/*
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
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EmergencyViewModel(
    private val respondToRequest: RespondToRequestUseCase,
    private val hasAlreadyResponded: HasAlreadyRespondedUseCase,
    // STEP 1: no longer used. The response count now comes from request.responseCount
    // (maintained by a Cloud Function), because only the owning hospital may list the responses.
    // Kept so the NavGraph code that creates this ViewModel does not need to change.
    @Suppress("unused")
    private val observeResponseCount: ObserveResponseCountUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(EmergencyState())
    val state: StateFlow<EmergencyState> = _state.asStateFlow()

    private val _effect = Channel<EmergencyEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: EmergencyIntent) {
        when (intent) {
            is EmergencyIntent.Respond -> respond(intent.requestId)
            is EmergencyIntent.RequestShown -> onRequestShown(intent.requestId)
        }
    }

    // ---------------------------------------------------------------
    // A card became visible: has the current donor already responded?
    // (retried the next time the card is shown, until it succeeds once)
    // ---------------------------------------------------------------
    private fun onRequestShown(requestId: String) {

        if (requestId in _state.value.respondedRequests) return

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

    // ---------------------------------------------------------------
    // The donor tapped "I can donate"
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

 */

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.social.vitadrop.domain.model.DonorResponse
import com.social.vitadrop.domain.model.ResponseStatus
import com.social.vitadrop.domain.usecase.DeclineRequestUseCase
import com.social.vitadrop.domain.usecase.GetMyResponseUseCase
import com.social.vitadrop.domain.usecase.RespondToRequestUseCase
import com.social.vitadrop.presentation.emergency.EmergencyEffect
import com.social.vitadrop.presentation.emergency.EmergencyIntent
import com.social.vitadrop.presentation.emergency.EmergencyState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * STEP 4: besides "I can donate", a donor can say "not available" and change their mind.
 * myResponses holds, for every request shown, the donor's answer and what the hospital recorded
 * (a key with a null value means: checked, no answer yet).
 *
 * EmergencyState.respondedRequests is still kept up to date (true while the answer is
 * "I can donate"), so nothing else that reads it breaks.
 */
class EmergencyViewModel(
    private val respondToRequest: RespondToRequestUseCase,
    private val declineRequest: DeclineRequestUseCase,
    private val getMyResponse: GetMyResponseUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(EmergencyState())
    val state: StateFlow<EmergencyState> = _state.asStateFlow()

    private val _myResponses = MutableStateFlow<Map<String, DonorResponse?>>(emptyMap())
    val myResponses: StateFlow<Map<String, DonorResponse?>> = _myResponses.asStateFlow()

    private val _effect = Channel<EmergencyEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: EmergencyIntent) {
        when (intent) {
            is EmergencyIntent.Respond -> answer(intent.requestId, accept = true)
            is EmergencyIntent.RequestShown -> onRequestShown(intent.requestId)
        }
    }

    /** "Not available", or "I can't donate any more" after saying yes. */
    fun decline(requestId: String) = answer(requestId, accept = false)

    // ---------------------------------------------------------------
    // A card became visible: what did this donor answer? (retried the next time the card is
    // shown, until it succeeds once)
    // ---------------------------------------------------------------
    private fun onRequestShown(requestId: String) {

        if (requestId in _myResponses.value) return

        viewModelScope.launch { refreshMyResponse(requestId) }
    }

    private suspend fun refreshMyResponse(requestId: String) {
        getMyResponse(requestId)
            .onSuccess { response ->
                _myResponses.update { it + (requestId to response) }
                _state.update {
                    it.copy(
                        respondedRequests = it.respondedRequests +
                                (requestId to (response?.responseStatus == ResponseStatus.ACCEPTED))
                    )
                }
            }
            .onFailure { Log.e(TAG, "Could not load the response", it) }
    }

    // ---------------------------------------------------------------
    // The donor tapped "I can donate" (accept) or "Not available" (decline)
    // ---------------------------------------------------------------
    private fun answer(requestId: String, accept: Boolean) {
        if (requestId in _state.value.respondingRequestIds) return   // ignore double taps

        viewModelScope.launch {
            _state.update { it.copy(respondingRequestIds = it.respondingRequestIds + requestId) }

            val result = if (accept) respondToRequest(requestId) else declineRequest(requestId)

            _state.update { it.copy(respondingRequestIds = it.respondingRequestIds - requestId) }

            result
                .onSuccess {
                    refreshMyResponse(requestId)
                    _effect.send(
                        EmergencyEffect.ShowMessage(
                            if (accept) "Response sent. Thank you for helping!"
                            else "Noted. Thank you for letting us know."
                        )
                    )
                }
                .onFailure { e ->
                    Log.e(TAG, "Answer failed", e)
                    _effect.send(
                        EmergencyEffect.ShowMessage(e.message ?: "Could not send your answer")
                    )
                }
        }
    }

    private companion object {
        const val TAG = "EmergencyVM"
    }
}