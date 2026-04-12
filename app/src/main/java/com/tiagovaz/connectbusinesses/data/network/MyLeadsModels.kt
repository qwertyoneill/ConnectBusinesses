package com.tiagovaz.connectbusinesses.data.network

import com.google.gson.annotations.SerializedName

data class MyLeadsResponse(
    val data: List<MyLeadItem>
)

data class MyLeadItem(
    val id: Int,
    val name: String,
    val description: String?,
    val type: String,
    val location: String?,
    val owner: String?,
    val created_at: String?,

    @SerializedName("background_file")
    val backgroundFile: String? = null
)