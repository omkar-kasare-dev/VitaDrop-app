package com.social.vitadrop.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.social.vitadrop.domain.usecase.LoginUseCase
import com.social.vitadrop.state.AuthState
import com.social.vitadrop.utils.SessionManager
import kotlinx.coroutines.launch

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    var state by mutableStateOf(AuthState())
        private set

    fun onEmailChange(value: String) {
        state = state.copy(email = value)
    }

    fun onPasswordChange(value: String) {
        state = state.copy(password = value)
    }

    fun onRoleSelected(role: String) {
        state = state.copy(role = role)
    }

    fun login(onSuccess: (String) -> Unit) {

        if (state.role.isBlank()) {
            state = state.copy(message = "Select role")
            return
        }

        if (state.email.isBlank() || state.password.isBlank()) {
            state = state.copy(message = "Email & Password required")
            return
        }

        viewModelScope.launch {

            state = state.copy(isLoading = true, message = "")

            val result = loginUseCase(
                state.email,
                state.password
            )

            state = state.copy(isLoading = false)

            result
                .onSuccess { role ->

                    if (role.isBlank()) {
                        state = state.copy(message = "Invalid role received")
                        return@onSuccess
                    }

                    sessionManager.saveUserSession( role)
                    onSuccess(role)
                }
                .onFailure { error ->
                    state = state.copy(
                        message = error.message ?: "Login Failed"
                    )
                }
        }
    }
}