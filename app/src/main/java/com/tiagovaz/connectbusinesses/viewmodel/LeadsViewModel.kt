package com.tiagovaz.connectbusinesses.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tiagovaz.connectbusinesses.data.network.DirectusRepository
import com.tiagovaz.connectbusinesses.data.network.LeadItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeadsViewModel @Inject constructor(
    private val repository: DirectusRepository
) : ViewModel() {

    private val _leads = MutableStateFlow<List<LeadItem>>(emptyList())
    val leads = _leads.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun fetchLeads(token: String) {
        Log.d("FEED_DEBUG", "TOKEN: $token")
        viewModelScope.launch {
            _isLoading.value = true
            _leads.value = repository.fetchFeedLeads(token)
            _isLoading.value = false
        }
    }

    fun sendSwipe(token: String, leadId: Int, direction: String) {
        viewModelScope.launch {
            val ok = repository.sendSwipe(token, leadId, direction)
            if (ok) {
                // remove da stack localmente (UX instantânea)
                _leads.value = _leads.value.filterNot { it.id == leadId }
            }
        }
    }
}
