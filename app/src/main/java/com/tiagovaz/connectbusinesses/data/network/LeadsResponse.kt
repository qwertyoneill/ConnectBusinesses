package com.tiagovaz.connectbusinesses.data.network

import com.google.gson.annotations.SerializedName

data class LeadsResponse(
    val data: List<LeadItem>
)

data class LeadItem(
    val id: Int,

    @SerializedName("name")
    val companyName: String?,

    @SerializedName("location")
    val city: String?,

    @SerializedName("description")
    val need: String?,

    @SerializedName("background_file")
    val backgroundFile: String?,

    @SerializedName("type")
    val type: String?,

    @SerializedName("owner")
    val owner: String?,

    @SerializedName("created_at")
    val createdAt: String?,

    @SerializedName("relevance_score")
    val relevanceScore: Int?
)

data class LeadDetailsResponse(
    val data: LeadItem?
)