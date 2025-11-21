package com.tiagovaz.connectbusinesses.data.network

data class CreateUserResponse(
    val data: CreatedUser?
)

data class CreatedUser(
    val id: String
)
