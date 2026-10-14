package com.social.vitadrop.presentation.profile


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.social.vitadrop.domain.model.UserRole
import com.social.vitadrop.domain.usecase.GetProfileUseCase
import com.social.vitadrop.domain.usecase.LogoutUseCase
import com.social.vitadrop.utils.SessionManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getProfile: GetProfileUseCase,
    private val logout: LogoutUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    private val _effect = Channel<ProfileEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var isRefreshing = false

    // Load once when the ViewModel is created (no more LaunchedEffect reload on every rotation)
    init {
        load()
    }

    fun onIntent(intent: ProfileIntent) {
        when (intent) {
            ProfileIntent.Refresh -> load()
            ProfileIntent.Logout -> performLogout()
        }
    }

    private fun load() {
        if (isRefreshing) return
        isRefreshing = true

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val result = getProfile()
            isRefreshing = false

            result
                .onSuccess { profile ->
                    _state.update { it.copy(profile = profile, isLoading = false) }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(isLoading = false, errorMessage = e.message ?: "Could not load your profile")
                    }
                }
        }
    }

    private fun performLogout() {
        if (_state.value.isLoggingOut) return

        viewModelScope.launch {
            _state.update { it.copy(isLoggingOut = true) }

            // The role comes from the loaded profile, or from the saved session if loading failed.
            val role = _state.value.profile?.role
                ?: UserRole.fromKey(sessionManager.getUserRole())

            logout(role)                    // removes the push token, then signs out of Firebase
            sessionManager.clearSession()   // forget the saved role / uid on this device

            _effect.send(ProfileEffect.LoggedOut)
        }
    }
}