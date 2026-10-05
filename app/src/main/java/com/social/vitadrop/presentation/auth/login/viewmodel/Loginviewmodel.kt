package com.social.vitadrop.presentation.auth.login.viewmodel


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.social.vitadrop.data.remote.FirebaseMessagingManager
import com.social.vitadrop.domain.model.UserRole
import com.social.vitadrop.domain.usecase.LoginUseCase
import com.social.vitadrop.presentation.auth.login.LoginEffect
import com.social.vitadrop.presentation.auth.login.LoginIntent
import com.social.vitadrop.presentation.auth.login.LoginState
import com.social.vitadrop.utils.SessionManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val sessionManager: SessionManager,
    // TEMPORARY: still a data-layer class. We tidy this in the notification cleanup batch.
    private val messagingManager: FirebaseMessagingManager
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    private val _effect = Channel<LoginEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.SelectRole ->
                _state.update { it.copy(role = intent.role, errorMessage = null) }

            is LoginIntent.EmailChanged ->
                _state.update { it.copy(email = intent.value) }

            is LoginIntent.PasswordChanged ->
                _state.update { it.copy(password = intent.value) }

            LoginIntent.Submit -> submit()
        }
    }

    private fun submit() {
        val s = _state.value
        if (s.isLoading) return

        val role = s.role ?: return showError("Select role")
        if (s.email.isBlank() || s.password.isBlank()) {
            return showError("Email & password required")
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            // The data layer verifies that this account really belongs to the selected role.
            val result = loginUseCase(s.email, s.password, role)

            _state.update { it.copy(isLoading = false) }

            result
                .onSuccess { uid ->
                    sessionManager.saveUserSession(uid, role.key)
                    messagingManager.generateAndSaveToken()
                    if (role == UserRole.DONOR) subscribeToDonorsTopic()
                    _effect.send(LoginEffect.LoggedIn(role))
                }
                .onFailure { error ->
                    _state.update { it.copy(errorMessage = error.message ?: "Login failed") }
                }
        }
    }

    // Fire and forget: navigation does not wait for this.
    private fun subscribeToDonorsTopic() {
        FirebaseMessaging.getInstance()
            .subscribeToTopic("donors")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) Log.d("FCM", "Subscribed to donors topic")
                else Log.e("FCM", "Subscription failed", task.exception)
            }
    }

    private fun showError(message: String) =
        _state.update { it.copy(errorMessage = message) }
}