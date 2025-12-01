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
                Log.e("AUTH_VM", "PROFILE = ${profileResponse}")
                Log.e("AUTH_VM", "FIRST = ${profileResponse?.data?.first_name}")
                Log.e("AUTH_VM", "LAST = ${profileResponse?.data?.last_name}")
                val user = profileResponse?.data

                val finalUserName = listOfNotNull(
                    user?.first_name,
                    user?.last_name
                ).joinToString(" ")

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
                    Log.e("AUTH_VM", "CHECK_LOGIN profile = $profile")
                    Log.e("AUTH_VM", "CHECK_LOGIN name = ${profile?.data?.first_name} ${profile?.data?.last_name}")

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

            if (_isLoading.value) return@launch   // evita cliques duplos

            // ⭐ VALIDAÇÕES
            if (firstName.isBlank() || lastName.isBlank()) {
                onError("Preenche o nome completo.")
                return@launch
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                onError("Email inválido.")
                return@launch
            }

            if (password.length < 6) {
                onError("A palavra-passe deve ter pelo menos 6 caracteres.")
                return@launch
            }

            _isLoading.value = true

            val result = repository.registerUser(
                firstName.trim(),
                lastName.trim(),
                email.trim(),
                password
            )

            _isLoading.value = false

            when (result) {
                true -> onSuccess()
                false -> onError("Não foi possível criar a conta. Tente outro email.")
            }
        }
    }


}
