package com.social.vitadrop.presentation.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.social.vitadrop.domain.model.User
import com.social.vitadrop.domain.usecase.RegisterUserUseCase
import com.social.vitadrop.presentation.event.RegisterEvent
import com.social.vitadrop.state.RegisterState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerUserUseCase: RegisterUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state

    fun onEvent(event: RegisterEvent) {
        when (event) {

            is RegisterEvent.UpdateField -> {
                _state.value = _state.value.copy(
                    name = event.name ?: _state.value.name,
                    email = event.email ?: _state.value.email,
                    phone = event.phone ?: _state.value.phone,
                    bloodGroup = event.bloodGroup ?: _state.value.bloodGroup,
                    city = event.city ?: _state.value.city,
                    address = event.address ?: _state.value.address,
                    password = event.password ?: _state.value.password,
                    role = event.role ?: _state.value.role
                )
            }

            RegisterEvent.Register -> register()
        }
    }

    private fun register() {
        val s = _state.value

        if (s.role.isEmpty()) {
            _state.value = s.copy(message = "Select role")
            return
        }

        if (s.email.isBlank() || s.password.isBlank()) {
            _state.value = s.copy(message = "Email & Password required")
            return
        }

        viewModelScope.launch {
            _state.value = s.copy(isLoading = true)

            val result = registerUserUseCase(
                User(
                    name = s.name,
                    email = s.email,
                    phone = s.phone,
                    role = s.role,
                    bloodGroup = s.bloodGroup,
                    city = s.city,
                    address = s.address
                ),
                s.password
            )

            _state.value = if (result.isSuccess) {
                s.copy(isLoading = false, message = "Registration Successful")
            } else {
                s.copy(isLoading = false, message = result.exceptionOrNull()?.message ?: "Registration Failed")
            }
        }
    }
}