package com.renz.planner.helper.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

class NoteEditorUtils {
    // Fungsi menyimpan file gambar dari galeri ke penyimpanan internal aplikasi
    fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val file = File(context.filesDir, "note_img_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            file.toURI().toString() // Menghasilkan URL file lokal permanen: file:///data/...
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}