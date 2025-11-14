package com.tiagovaz.connectbusinesses.data.network

data class LeadsResponse(val data: List<LeadItem>)

data class LeadItem(
    val id: String,
    val companyName: String?,
    val city: String?,
    val need: String?,
    val imageUrl: String?
)

data class LeadDetailsResponse(
    val data: LeadItem?
)