package com.examhacker.common.utility

import android.content.Context
import android.net.Uri
import java.io.File

interface IFileResolver {
    fun getFileFromUri(uri: Uri): File?
}

class FileResolver(): IFileResolver {

    override fun getFileFromUri(uri: Uri): File? {
        return uri.path?.let {
            File(it)
        }
    }
}