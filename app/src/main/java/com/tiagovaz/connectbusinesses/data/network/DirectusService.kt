package com.tiagovaz.connectbusinesses.data.network

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
    ): Response<UserResponse>

    @POST("auth/refresh")
    suspend fun refreshToken(
        @Body body: Map<String, String>
    ): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body body: Map<String, Any>): Response<Unit>

}
