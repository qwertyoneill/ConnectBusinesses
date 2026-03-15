package com.tiagovaz.connectbusinesses.data.network

import com.tiagovaz.connectbusinesses.data.network.auth.FirebaseLoginRequest
import com.tiagovaz.connectbusinesses.data.network.auth.FirebaseLoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DirectusRepository @Inject constructor(
    private val api: DirectusService
) {
    suspend fun sendSwipe(
        token: String,
        leadId: Int,
        direction: String
    ): Boolean = try {
        api.createSwipe(
            token = "Bearer $token",
            body = SwipeCreateRequest(
                direction = direction,
                lead = leadId
            )
        ).isSuccessful
    } catch (e: Exception) {
        false
    }
    suspend fun fetchMatches(token: String): Result<List<MatchViewItem>> {
        return try {
            val response = api.getMyMatches("Bearer $token")

            if (response.isSuccessful) {
                val matches = response.body()?.data ?: emptyList()
                Result.success(matches)
            } else {
                Result.failure(
                    Exception(
                        "Erro ${response.code()}: ${response.errorBody()?.string()}"
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    // ---------------- AUTH / PERFIL ----------------

    suspend fun login(email: String, password: String): LoginResponse? = try {
        val response = api.login(LoginRequest(email, password))
        if (response.isSuccessful) response.body() else null
    } catch (e: Exception) { null }

    suspend fun fetchLeadDetails(token: String, leadId: String) = try {
        api.getLeadDetails("Bearer $token", leadId).body()?.data
    } catch (e: Exception) { null }

    suspend fun getMe(token: String): UserProfileResponse? = try {
        val response = api.getMe("Bearer $token")
        if (response.isSuccessful) response.body() else null
    } catch (e: Exception) { null }

    suspend fun registerUser(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Boolean = try {
        api.register(
            CreateUserRequest(email, password, firstName, lastName)
        ).isSuccessful
    } catch (e: Exception) { false }

    suspend fun firebaseLogin(idToken: String): FirebaseLoginResponse? = try {
        val response = api.firebaseLogin(FirebaseLoginRequest(idToken))
        if (response.isSuccessful) response.body() else null
    } catch (e: Exception) { null }

    suspend fun fetchFeedLeads(token: String, limit: Int = 20): List<LeadItem> = try {
        val response = api.getFeedLeads("Bearer $token", limit)

        android.util.Log.d("FEED_RESULT", "CODE: ${response.code()}")

        if (response.isSuccessful) {
            val data = response.body()?.data ?: emptyList()
            android.util.Log.d("FEED_RESULT", "LEADS SIZE: ${data.size}")
            data
        } else {
            android.util.Log.d("FEED_RESULT", "ERROR BODY: ${response.errorBody()?.string()}")
            emptyList()
        }
    } catch (e: Exception) {
        android.util.Log.e("FEED_RESULT", "EXCEPTION: ${e.message}", e)
        emptyList()
    }

}
