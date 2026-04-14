package com.tiagovaz.connectbusinesses.data.network

import com.tiagovaz.connectbusinesses.data.network.auth.FirebaseLoginRequest
import com.tiagovaz.connectbusinesses.data.network.auth.FirebaseLoginResponse
import retrofit2.Response
import retrofit2.http.*
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.Part

interface DirectusService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("items/leads/{id}")
    suspend fun getLeadDetails(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Query("fields") fields: String = "id,name,description,type,location,owner,created_at,background_file"
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

    @GET("matches")
    suspend fun getMatches(
        @Header("Authorization") token: String
    ): Response<MatchesViewResponse>

    @GET("feed")
    suspend fun getFeedLeads(
        @Header("Authorization") token: String,
        @Query("limit") limit: Int = 20
    ): Response<LeadsResponse>

    @GET("chat/conversations")
    suspend fun getConversations(
        @Header("Authorization") token: String
    ): Response<ConversationsResponse>

    @GET("chat/conversations/{id}/messages")
    suspend fun getConversationMessages(
        @Header("Authorization") token: String,
        @Path("id") conversationId: Int
    ): Response<ConversationMessagesResponse>

    @POST("chat/conversations/{id}/messages")
    suspend fun sendConversationMessage(
        @Header("Authorization") token: String,
        @Path("id") conversationId: Int,
        @Body body: SendMessageRequest
    ): Response<SendMessageResponse>

    @POST("chat/conversations/{id}/read")
    suspend fun markConversationRead(
        @Header("Authorization") token: String,
        @Path("id") conversationId: Int
    ): Response<MarkConversationReadResponse>

    @POST("chat/matches/{matchId}/open")
    suspend fun openConversationFromMatch(
        @Header("Authorization") token: String,
        @Path("matchId") matchId: Int
    ): Response<OpenConversationResponse>

    @POST("items/leads")
    suspend fun createLead(
        @Header("Authorization") token: String,
        @Body body: CreateLeadRequest
    ): Response<CreateLeadResponse>

    @GET("items/leads")
    suspend fun getMyLeads(
        @Header("Authorization") token: String,
        @Query("filter[owner][_eq]") ownerId: String,
        @Query("fields") fields: String = "id,name,description,type,location,owner,created_at,background_file",
        @Query("sort") sort: String = "-created_at"
    ): Response<MyLeadsResponse>

    @PATCH("items/leads/{id}")
    suspend fun updateLead(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body body: CreateLeadRequest
    ): Response<CreateLeadResponse>

    @DELETE("items/leads/{id}")
    suspend fun deleteLead(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>

    @GET("chat/leads/{id}/interested")
    suspend fun getInterestedInLead(
        @Header("Authorization") token: String,
        @Path("id") leadId: Int
    ): Response<InterestedInLeadResponse>

    @POST("chat/leads/{leadId}/interested/{userId}/accept")
    suspend fun acceptInterestedInLead(
        @Header("Authorization") token: String,
        @Path("leadId") leadId: Int,
        @Path("userId") userId: String
    ): Response<AcceptInterestedResponse>

    @Multipart
    @POST("files")
    suspend fun uploadFile(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Response<UploadFileResponse>
}
