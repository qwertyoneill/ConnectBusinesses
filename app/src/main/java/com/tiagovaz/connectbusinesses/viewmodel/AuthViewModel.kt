package com.tiagovaz.connectbusinesses.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tiagovaz.connectbusinesses.data.network.DirectusRepository
import com.tiagovaz.connectbusinesses.data.storage.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: DirectusRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _loginError = MutableStateFlow("")
    val loginError = _loginError.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    private val _token = MutableStateFlow<String?>(null)
    val token = _token.asStateFlow()

    private val _userName = MutableStateFlow<String?>(null)
    val userName = _userName.asStateFlow()

    init {
        checkLoginStatus()
    }

    fun login() {
        viewModelScope.launch {
            _isLoading.value = true
            _loginError.value = ""

            val loginResponse = repository.login(email.value, password.value)
            val authToken = loginResponse?.data?.access_token
            val refreshToken = loginResponse?.data?.refresh_token

            if (authToken != null) {
                dataStoreManager.saveToken(authToken)
                if (refreshToken != null) dataStoreManager.saveRefreshToken(refreshToken)
                _token.value = authToken
                _isLoggedIn.value = true

                val profileResponse = repository.getMe(authToken)
                val finalUserName = profileResponse?.data?.first_name ?: ""

                dataStoreManager.saveUserName(finalUserName)
                _userName.value = finalUserName
                Log.d("AUTH_VM", "User logged in: $finalUserName")
            } else {
                _loginError.value = "Email ou password incorretos"
            }

            _isLoading.value = false
        }
    }

    fun checkLoginStatus() {
        viewModelScope.launch {
            val savedToken = dataStoreManager.token.first()
            if (!savedToken.isNullOrEmpty()) {
                val profile = repository.getMe(savedToken)
                if (profile != null) {
                    _token.value = savedToken
                    _isLoggedIn.value = true
                    _userName.value = dataStoreManager.userName.first()
                } else {
                    dataStoreManager.clearAll()
                    _isLoggedIn.value = false
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            dataStoreManager.clearAll()
            _isLoggedIn.value = false
            _token.value = null
            _userName.value = null
            clearLoginFields()
        }
    }

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun clearLoginFields() {
        _email.value = ""
        _password.value = ""
        _loginError.value = ""
    }
    fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {

            val result = repository.registerUser(
                firstName,
                lastName,
                email,
                password
            )

            if (result) onSuccess()
            else onError("Erro ao criar conta")
        }
    }

}
