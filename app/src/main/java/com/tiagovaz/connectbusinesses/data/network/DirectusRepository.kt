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
            val response = api.getMatches("Bearer $token")

            if (response.isSuccessful) {
                Result.success(response.body()?.data ?: emptyList())
            } else {
                Result.failure(
                    Exception("Erro ${response.code()}: ${response.errorBody()?.string()}")
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

    suspend fun fetchConversations(token: String): Result<List<ConversationItem>> {
        return try {
            val response = api.getConversations("Bearer $token")

            if (response.isSuccessful) {
                Result.success(response.body()?.data ?: emptyList())
            } else {
                Result.failure(
                    Exception("Erro ${response.code()}: ${response.errorBody()?.string()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchConversationMessages(
        token: String,
        conversationId: Int
    ): Result<List<ConversationMessageItem>> {
        return try {
            val response = api.getConversationMessages("Bearer $token", conversationId)

            if (response.isSuccessful) {
                Result.success(response.body()?.data ?: emptyList())
            } else {
                Result.failure(
                    Exception("Erro ${response.code()}: ${response.errorBody()?.string()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendConversationMessage(
        token: String,
        conversationId: Int,
        message: String
    ): Result<ConversationMessageItem> {
        return try {
            val response = api.sendConversationMessage(
                token = "Bearer $token",
                conversationId = conversationId,
                body = SendMessageRequest(message)
            )

            if (response.isSuccessful) {
                val responseBody = response.body()

                if (responseBody?.data != null) {
                    Result.success(responseBody.data)
                } else {
                    Result.failure(Exception("Resposta vazia"))
                }
            } else {
                Result.failure(
                    Exception("Erro ${response.code()}: ${response.errorBody()?.string()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markConversationAsRead(
        token: String,
        conversationId: Int
    ): Result<Boolean> {
        return try {
            val response = api.markConversationRead("Bearer $token", conversationId)

            if (response.isSuccessful) {
                Result.success(response.body()?.success == true)
            } else {
                Result.failure(
                    Exception("Erro ${response.code()}: ${response.errorBody()?.string()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun openConversationFromMatch(
        token: String,
        matchId: Int
    ): Result<Int> {
        return try {
            val response = api.openConversationFromMatch("Bearer $token", matchId)

            if (response.isSuccessful) {
                val conversationId = response.body()?.data?.conversation_id
                if (conversationId != null) {
                    Result.success(conversationId)
                } else {
                    Result.failure(Exception("Resposta sem conversation_id"))
                }
            } else {
                Result.failure(
                    Exception("Erro ${response.code()}: ${response.errorBody()?.string()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun createLead(
        token: String,
        name: String,
        type: String,
        description: String?,
        location: String?
    ): Result<CreatedLeadItem> {
        return try {
            val response = api.createLead(
                token = "Bearer $token",
                body = CreateLeadRequest(
                    name = name.trim(),
                    type = type.trim(),
                    description = description?.trim()?.ifBlank { null },
                    location = location?.trim()?.ifBlank { null }
                )
            )

            if (response.isSuccessful) {
                val data = response.body()?.data
                if (data != null) {
                    Result.success(data)
                } else {
                    Result.failure(Exception("Resposta vazia ao criar lead"))
                }
            } else {
                Result.failure(
                    Exception("Erro ${response.code()}: ${response.errorBody()?.string()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun fetchMyLeads(
        token: String,
        ownerId: String
    ): Result<List<MyLeadItem>> {
        return try {
            val response = api.getMyLeads(
                token = "Bearer $token",
                ownerId = ownerId
            )

            if (response.isSuccessful) {
                Result.success(response.body()?.data ?: emptyList())
            } else {
                Result.failure(
                    Exception("Erro ${response.code()}: ${response.errorBody()?.string()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun updateLead(
        token: String,
        id: Int,
        name: String,
        type: String,
        description: String?,
        location: String?
    ): Result<CreatedLeadItem> {
        return try {
            val response = api.updateLead(
                token = "Bearer $token",
                id = id,
                body = CreateLeadRequest(
                    name = name.trim(),
                    type = type.trim(),
                    description = description?.trim()?.ifBlank { null },
                    location = location?.trim()?.ifBlank { null }
                )
            )

            if (response.isSuccessful) {
                val data = response.body()?.data
                if (data != null) {
                    Result.success(data)
                } else {
                    Result.failure(Exception("Resposta vazia ao editar lead"))
                }
            } else {
                Result.failure(
                    Exception("Erro ${response.code()}: ${response.errorBody()?.string()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun deleteLead(
        token: String,
        id: Int
    ): Result<Unit> {
        return try {
            val response = api.deleteLead(
                token = "Bearer $token",
                id = id
            )
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(
                    Exception("Erro ${response.code()}: ${response.errorBody()?.string()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
