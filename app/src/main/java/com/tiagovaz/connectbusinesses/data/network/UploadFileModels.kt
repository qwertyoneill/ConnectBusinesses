package com.tiagovaz.connectbusinesses.data.network

data class UploadFileResponse(
    val data: UploadedFileItem
)

data class UploadedFileItem(
    val id: String
)