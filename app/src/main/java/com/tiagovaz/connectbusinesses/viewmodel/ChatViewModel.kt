package com.tiagovaz.connectbusinesses.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tiagovaz.connectbusinesses.data.network.DirectusRepository
import com.tiagovaz.connectbusinesses.data.storage.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: DirectusRepository,
    private val dataStore: DataStoreManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState = _uiState.asStateFlow()

    fun loadMessages(conversationId: Int) {
        viewModelScope.launch {
            val token = dataStore.getAccessToken()

            if (token.isNullOrBlank()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Inicia sessão para abrir o chat."
                    )
                }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, error = null) }

            repository.fetchConversationMessages(token, conversationId)
                .onSuccess { messages ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            messages = messages,
                            error = null
                        )
                    }

                    repository.markConversationAsRead(token, conversationId)
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Erro ao carregar mensagens"
                        )
                    }
                }
        }
    }

    fun sendMessage(conversationId: Int, text: String) {
        val trimmed = text.trim()
        if (trimmed.isBlank()) return

        viewModelScope.launch {
            val token = dataStore.getAccessToken() ?: return@launch

            _uiState.update { it.copy(sending = true, error = null) }

            repository.sendConversationMessage(token, conversationId, trimmed)
                .onSuccess { sentMessage ->
                    _uiState.update {
                        it.copy(
                            sending = false,
                            messages = it.messages + sentMessage
                        )
                    }
                    repository.markConversationAsRead(token, conversationId)
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            sending = false,
                            error = e.message ?: "Erro ao enviar mensagem"
                        )
                    }
                }
        }
    }
}