package com.tiagovaz.connectbusinesses.data.network.auth

data class FirebaseLoginRequest(
    val idToken: String
)

data class FirebaseLoginResponse(
    val ok: Boolean,
    val access_token: String,
    val user: FirebaseLoginUser?
)

data class FirebaseLoginUser(
    val id: String,
    val email: String?,
    val first_name: String?,
    val last_name: String?,
    val role: String?
)
