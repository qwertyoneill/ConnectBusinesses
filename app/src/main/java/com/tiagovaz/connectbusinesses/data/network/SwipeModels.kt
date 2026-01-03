package com.tiagovaz.connectbusinesses.data.network

data class SwipeCreateRequest(
    val direction: String, // "like" | "pass"
    val lead: Int
)

data class SwipeCreateResponse(
    val data: SwipeCreatedItem?
)

data class SwipeCreatedItem(
    val id: Int?
)

data class SwipesResponse(
    val data: List<SwipeItem>
)

data class SwipeItem(
    val lead: Int
)
