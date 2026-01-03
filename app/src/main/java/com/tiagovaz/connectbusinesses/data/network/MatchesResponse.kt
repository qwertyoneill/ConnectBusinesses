package com.tiagovaz.connectbusinesses.data.network

data class MatchesResponse(
    val data: List<MatchItem>
)

data class MatchItem(
    val id: Int,
    val lead: LeadItem,
    val user_a: String,
    val user_b: String,
    val created_at: String
)
