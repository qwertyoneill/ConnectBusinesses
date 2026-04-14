package com.tiagovaz.connectbusinesses.utils

import android.net.Uri

object LeadImageUtils {

    private const val BASE_URL = "https://directus.qwertyoneill.xyz"

    fun buildLeadImageUrl(
        fileId: String?,
        accessToken: String? = null
    ): String? {
        val cleanId = fileId?.trim()

        if (cleanId.isNullOrEmpty()) return null

        val base = "$BASE_URL/assets/$cleanId"

        return if (accessToken.isNullOrBlank()) {
            "$base?width=1200&height=675&fit=cover"
        } else {
            "$base?access_token=${Uri.encode(accessToken)}&width=1200&height=675&fit=cover"
        }
    }
}