package com.tiagovaz.connectbusinesses.utils

object LeadImageUtils {

    private const val BASE_URL = "https://directus.qwertyoneill.xyz"

    fun buildLeadImageUrl(fileId: String?): String? {
        val cleanId = fileId?.trim()

        if (cleanId.isNullOrEmpty()) return null

        return "$BASE_URL/assets/$cleanId?width=1200&height=675&fit=cover"
    }
}