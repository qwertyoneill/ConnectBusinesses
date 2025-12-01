package com.tiagovaz.connectbusinesses.data.network

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val data: LoginData?
)

data class LoginData(
    val access_token: String,
    val refresh_token: String?,
    val expires: Long?,
    val user: LoginUserData?
)

data class LoginUserData(
    val id: String?,
    val first_name: String?,
    val last_name: String?,
    val email: String?
)
