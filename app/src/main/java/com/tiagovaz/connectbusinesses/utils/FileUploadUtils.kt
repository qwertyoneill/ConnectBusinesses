package com.tiagovaz.connectbusinesses.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

object FileUploadUtils {

    private fun getFileName(context: Context, uri: Uri): String {
        var fileName = "upload_image.jpg"

        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (it.moveToFirst() && nameIndex >= 0) {
                fileName = it.getString(nameIndex)
            }
        }

        return fileName
    }

    fun uriToMultipart(context: Context, uri: Uri): MultipartBody.Part {
        val fileName = getFileName(context, uri)
        val tempFile = File(context.cacheDir, fileName)

        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }

        val mimeType = context.contentResolver.getType(uri) ?: "image/*"
        val requestBody = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())

        return MultipartBody.Part.createFormData(
            "file",
            tempFile.name,
            requestBody
        )
    }
}