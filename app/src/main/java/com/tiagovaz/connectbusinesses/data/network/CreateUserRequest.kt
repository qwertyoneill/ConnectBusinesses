package com.tiagovaz.connectbusinesses.data.network

data class CreateUserRequest(
    val email: String,
    val password: String,
    val first_name: String,
    val last_name: String
)
