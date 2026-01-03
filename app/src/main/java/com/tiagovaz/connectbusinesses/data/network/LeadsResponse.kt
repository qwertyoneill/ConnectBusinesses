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

    @SerializedName("imageUrl")
    val imageUrl: String?
)
data class LeadDetailsResponse(
    val data: LeadItem?
)
