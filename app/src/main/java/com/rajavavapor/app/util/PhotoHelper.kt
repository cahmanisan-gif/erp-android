package com.rajavavapor.app.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PhotoHelper {

    fun createTempPhotoFile(context: Context): File {
        val dir = File(context.cacheDir, "photos")
        if (!dir.exists()) dir.mkdirs()
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return File(dir, "selfie_$timestamp.jpg")
    }

    fun getPhotoUri(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    fun deletePhotoFile(file: File?) {
        file?.takeIf { it.exists() }?.delete()
    }
}
