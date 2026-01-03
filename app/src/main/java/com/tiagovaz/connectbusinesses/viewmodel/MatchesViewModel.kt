package com.tiagovaz.connectbusinesses.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tiagovaz.connectbusinesses.data.network.DirectusRepository
import com.tiagovaz.connectbusinesses.data.network.MatchItem
import com.tiagovaz.connectbusinesses.data.storage.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MatchesViewModel @Inject constructor(
    private val repository: DirectusRepository
) : ViewModel() {
    private val _matches = MutableStateFlow<List<MatchItem>>(emptyList())
    val matches = _matches.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow() // 👈 OBRIGATÓRIO
    private val _hasNewMatch = MutableStateFlow(false)
    val hasNewMatch = _hasNewMatch.asStateFlow()
    fun loadMatches(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.fetchMatches(token)
            if (_matches.value.isNotEmpty().not() && result.isNotEmpty()) {
                _hasNewMatch.value = true
            }
            _matches.value = result
            _isLoading.value = false
        }
    }
    fun markMatchesAsSeen() {
        _hasNewMatch.value = false
    }
}