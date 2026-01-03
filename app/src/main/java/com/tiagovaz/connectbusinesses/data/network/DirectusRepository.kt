package com.tiagovaz.connectbusinesses.data.network

import com.tiagovaz.connectbusinesses.data.network.auth.FirebaseLoginRequest
import com.tiagovaz.connectbusinesses.data.network.auth.FirebaseLoginResponse
import javax.inject.Inject

class DirectusRepository @Inject constructor(
    private val api: DirectusService
) {

    suspend fun fetchLeads(token: String): List<LeadItem> = try {
        api.getLeads("Bearer $token").body()?.data ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    suspend fun fetchLeadsUnswiped(token: String): List<LeadItem> = try {
        val leads = fetchLeads(token)

        // swipes já feitos pelo utilizador (policy resolve segurança)
        val swipedIds = api.getMySwipes("Bearer $token")
            .body()?.data
            ?.map { it.lead }
            ?.toSet()
            ?: emptySet()

        leads.filterNot { it.id in swipedIds }
    } catch (e: Exception) {
        emptyList()
    }

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

    suspend fun fetchMatches(token: String): List<MatchItem> = try {
        api.getMyMatches("Bearer $token")
            .body()
            ?.data
            ?: emptyList()
    } catch (e: Exception) {
        emptyList()
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

    suspend fun refreshToken(refreshToken: String): String? = try {
        api.refreshToken(mapOf("refresh_token" to refreshToken))
            .body()?.data?.access_token
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
}
