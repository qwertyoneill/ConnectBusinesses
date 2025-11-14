package com.tiagovaz.connectbusinesses.data.network

import android.util.Log
import javax.inject.Inject

class DirectusRepository @Inject constructor(
    private val api: DirectusService
) {
    suspend fun login(email: String, password: String): LoginResponse? {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }



    suspend fun fetchLeads(token: String): List<LeadItem>? = try {
        val response = api.getLeads("Bearer $token")
        response.body()?.data
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    suspend fun fetchLeadDetails(token: String, leadId: String): LeadItem? = try {
        val response = api.getLeadDetails("Bearer $token", leadId)
        response.body()?.data
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
    suspend fun getMe(token: String): UserResponse? = try {
        val response = api.getMe("Bearer $token")
        response.body()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
    suspend fun refreshToken(refreshToken: String): String? = try {
        val body = mapOf("refresh_token" to refreshToken)
        val response = api.refreshToken(body)
        if (response.isSuccessful) {
            response.body()?.data?.access_token
        } else null
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
    suspend fun registerUser(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Boolean {
        return try {
            val body = mapOf(
                "email" to email,
                "password" to password,
                "first_name" to firstName,
                "last_name" to lastName
            )
            val response = api.register(body)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


}