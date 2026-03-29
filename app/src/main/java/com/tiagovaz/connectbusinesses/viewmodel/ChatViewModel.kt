package com.tiagovaz.connectbusinesses.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tiagovaz.connectbusinesses.data.network.DirectusRepository
import com.tiagovaz.connectbusinesses.data.storage.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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

    private var pollingJob: Job? = null
    private var currentConversationId: Int? = null

    fun openChat(conversationId: Int) {
        currentConversationId = conversationId

        viewModelScope.launch {
            val token = dataStore.getAccessToken()
            val userId = dataStore.getUserId()

            if (token.isNullOrBlank()) {
                _uiState.update {
                    it.copy(
                        initialLoading = false,
                        error = "Inicia sessão para abrir o chat."
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(
                    currentUserId = userId,
                    initialLoading = true,
                    error = null,
                    sendError = null
                )
            }

            repository.fetchConversationMessages(token, conversationId)
                .onSuccess { messages ->
                    _uiState.update {
                        it.copy(
                            initialLoading = false,
                            messages = messages,
                            error = null
                        )
                    }

                    repository.markConversationAsRead(token, conversationId)
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            initialLoading = false,
                            error = e.message ?: "Erro ao carregar mensagens"
                        )
                    }
                }
        }
    }

    fun refreshMessages() {
        val conversationId = currentConversationId ?: return

        viewModelScope.launch {
            val token = dataStore.getAccessToken()
            if (token.isNullOrBlank()) return@launch

            _uiState.update { it.copy(refreshing = true) }

            repository.fetchConversationMessages(token, conversationId)
                .onSuccess { messages ->
                    _uiState.update {
                        it.copy(
                            refreshing = false,
                            messages = messages,
                            error = null
                        )
                    }

                    repository.markConversationAsRead(token, conversationId)
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            refreshing = false,
                            error = e.message ?: "Erro ao atualizar mensagens"
                        )
                    }
                }
        }
    }

    fun sendMessage(text: String) {
        val conversationId = currentConversationId ?: return
        val trimmed = text.trim()
        if (trimmed.isBlank()) return

        viewModelScope.launch {
            val token = dataStore.getAccessToken()

            if (token.isNullOrBlank()) {
                _uiState.update {
                    it.copy(
                        sending = false,
                        sendError = "Inicia sessão para enviar mensagens."
                    )
                }
                return@launch
            }

            _uiState.update { it.copy(sending = true, sendError = null) }

            repository.sendConversationMessage(token, conversationId, trimmed)
                .onSuccess {
                    _uiState.update { it.copy(sending = false) }
                    refreshMessages()
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            sending = false,
                            sendError = e.message ?: "Erro ao enviar mensagem"
                        )
                    }
                }
        }
    }

    fun startPolling(intervalMs: Long = 3000L) {
        if (pollingJob?.isActive == true) return

        pollingJob = viewModelScope.launch {
            while (true) {
                delay(intervalMs)
                refreshMessages()
            }
        }
    }

    fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    override fun onCleared() {
        stopPolling()
        super.onCleared()
    }
}