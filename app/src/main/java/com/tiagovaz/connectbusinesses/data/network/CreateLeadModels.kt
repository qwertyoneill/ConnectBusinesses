package com.tiagovaz.connectbusinesses.data.network

data class CreateLeadRequest(
    val name: String,
    val type: String,
    val description: String?,
    val location: String?
)

data class CreateLeadResponse(
    val data: CreatedLeadItem
)

data class CreatedLeadItem(
    val id: Int,
    val name: String?,
    val type: String?,
    val description: String?,
    val location: String?,
    val owner: String?,
    val created_at: String?
)