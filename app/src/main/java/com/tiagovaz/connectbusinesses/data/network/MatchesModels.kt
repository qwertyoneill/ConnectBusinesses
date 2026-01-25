package com.tiagovaz.connectbusinesses.data.network

data class MatchesViewResponse(
    val data: List<MatchViewItem>
)

data class MatchViewItem(
    val id: Int,
    val lead_id: Int?,
    val lead_name: String?,
    val matched_at: String?,
    val match_id: Int?,
    val other_user: String?,
    val other_first_name: String?,
    val other_last_name: String?,
    val other_email: String?,
    val other_avatar: String?
)