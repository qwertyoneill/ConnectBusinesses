package com.tiagovaz.connectbusinesses.data.network

import com.tiagovaz.connectbusinesses.data.network.auth.FirebaseLoginRequest
import com.tiagovaz.connectbusinesses.data.network.auth.FirebaseLoginResponse
import retrofit2.Response
import retrofit2.http.*

interface DirectusService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("items/leads")
    suspend fun getLeads(
        @Header("Authorization") token: String
    ): Response<LeadsResponse>

    @GET("items/leads/{id}")
    suspend fun getLeadDetails(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<LeadDetailsResponse>

    @GET("users/me")
    suspend fun getMe(
        @Header("Authorization") token: String
    ): Response<UserProfileResponse>

    @POST("auth/refresh")
    suspend fun refreshToken(
        @Body body: Map<String, String>
    ): Response<LoginResponse>

    @POST("users")
    suspend fun register(
        @Body request: CreateUserRequest
    ): Response<CreateUserResponse>

    // Firebase
    @POST("firebase-login")
    suspend fun firebaseLogin(
        @Body request: FirebaseLoginRequest
    ): Response<FirebaseLoginResponse>

    // Swipes do utilizador atual
    @GET("items/swipes")
    suspend fun getMySwipes(
        @Header("Authorization") token: String,
        @Query("fields") fields: String = "lead"
    ): Response<SwipesResponse>

    // Criar swipe
    @POST("items/swipes")
    suspend fun createSwipe(
        @Header("Authorization") token: String,
        @Body body: SwipeCreateRequest
    ): Response<SwipeCreateResponse>

    @GET("items/matches")
    suspend fun getMyMatches(
        @Header("Authorization") token: String
    ): Response<MatchesResponse>

}
