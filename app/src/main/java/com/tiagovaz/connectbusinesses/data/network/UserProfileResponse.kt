package com.tiagovaz.connectbusinesses.data.network

data class UserProfileResponse(
    val data: ProfileUserData?
)

data class ProfileUserData(
    val id: String?,
    val first_name: String?,
    val last_name: String?,
    val email: String?,
    val role: String?
)
