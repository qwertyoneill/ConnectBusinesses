package com.tiagovaz.connectbusinesses.data.network

import android.util.Log
import com.tiagovaz.connectbusinesses.data.network.auth.FirebaseLoginRequest
import com.tiagovaz.connectbusinesses.data.network.auth.FirebaseLoginResponse
import javax.inject.Inject

class DirectusRepository @Inject constructor(
    private val api: DirectusService
) {

    suspend fun login(email: String, password: String): LoginResponse? {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }
    suspend fun fetchLeads(token: String) = try {
        api.getLeads("Bearer $token").body()?.data
    } catch (e: Exception) { null }
    suspend fun fetchLeadDetails(token: String, leadId: String) = try {
        api.getLeadDetails("Bearer $token", leadId).body()?.data
    } catch (e: Exception) { null }
    suspend fun getMe(token: String): UserProfileResponse? {
        return try {
            val response = api.getMe("Bearer $token")
            if (response.isSuccessful) response.body()
            else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun refreshToken(refreshToken: String): String? = try {
        api.refreshToken(mapOf("refresh_token" to refreshToken))
            .body()?.data?.access_token
    } catch (e: Exception) { null }
    suspend fun registerUser(firstName: String,lastName: String, email: String, password: String): Boolean {
        return try {
            val request = CreateUserRequest(
                email = email,
                password = password,
                first_name = firstName,
                last_name = lastName
            )

            val response = api.register(request)

            Log.e("REGISTER", "code = ${response.code()}")
            Log.e("REGISTER", "body = ${response.body()}")
            Log.e("REGISTER", "errorBody = ${response.errorBody()?.string()}")

            response.isSuccessful

        } catch (e: Exception) {
            Log.e("REGISTER", "Exception: ${e.message}")
            false
        }
    }
    suspend fun firebaseLogin(idToken: String): FirebaseLoginResponse? {
        return try {
            val response = api.firebaseLogin(FirebaseLoginRequest(idToken))
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }
}
