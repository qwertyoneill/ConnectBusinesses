package com.tiagovaz.connectbusinesses.utils

import android.net.Uri

object LeadImageUtils {

    private const val BASE_URL = "https://directus.qwertyoneill.xyz"

    fun buildLeadImageUrl(
        fileId: String?,
        accessToken: String? = null,
        width: Int = 900,
        height: Int = 1600,
        quality: Int = 82
    ): String? {
        val cleanId = fileId?.trim()
        if (cleanId.isNullOrEmpty()) return null

        val params = buildList {
            add("width=$width")
            add("height=$height")
            add("fit=cover")
            add("quality=$quality")
            if (!accessToken.isNullOrBlank()) {
                add("access_token=${Uri.encode(accessToken)}")
            }
        }.joinToString("&")

        return "$BASE_URL/assets/$cleanId?$params"
    }
}