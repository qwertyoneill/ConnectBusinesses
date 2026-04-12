package com.tiagovaz.connectbusinesses.data.network

data class CreateLeadRequest(
    val name: String,
    val type: String,
    val description: String?,
    val location: String?,
    val background_file: String? = null
)

data class CreatedLeadItem(
    val id: Int,
    val name: String,
    val type: String,
    val description: String?,
    val location: String?,
    val owner: String?,
    val created_at: String?,
    val background_file: String? = null
)

data class CreateLeadResponse(
    val data: CreatedLeadItem
)