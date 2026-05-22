package com.social.vitadrop.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.social.vitadrop.data.ai.GeminiManager
import com.social.vitadrop.domain.model.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val geminiManager = GeminiManager()

    private val _messages =
        MutableStateFlow<List<ChatMessage>>(emptyList())

    val messages =
        _messages.asStateFlow()

    fun sendMessage(
        userMessage: String
    ) {

        if (userMessage.isBlank()) return

        val updatedList =
            _messages.value.toMutableList()

        updatedList.add(
            ChatMessage(
                text = userMessage,
                isUser = true
            )
        )

        _messages.value = updatedList

        viewModelScope.launch {

            val response =
                geminiManager.generateResponse(userMessage)

            val aiUpdatedList =
                _messages.value.toMutableList()

            aiUpdatedList.add(
                ChatMessage(
                    text = response,
                    isUser = false
                )
            )

            _messages.value = aiUpdatedList
        }
    }
}