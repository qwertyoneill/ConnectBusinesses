package com.tiagovaz.connectbusinesses.data.network

// LoginRequest.kt
data class LoginRequest(val email: String, val password: String)

// LoginResponse.kt
data class LoginResponse(
    val data: LoginData?
)

data class LoginData(
    val access_token: String,
    val refresh_token: String?,
    val expires: Long?,
    val user: UserData?
)

data class UserData(
    val first_name: String,
    val last_name: String
)