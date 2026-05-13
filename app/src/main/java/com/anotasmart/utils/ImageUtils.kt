package com.anotasmart.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object ImageUtils {
    /**
     * Copia uma imagem de uma URI para o armazenamento interno do app,
     * aplicando compressão para economizar espaço.
     * Retorna o caminho absoluto do novo arquivo ou null em caso de erro.
     */
    fun saveImageToInternalStorage(context: Context, uri: Uri, folderName: String, prefix: String): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            // Criar diretório se não existir
            val directory = File(context.filesDir, folderName)
            if (!directory.exists()) {
                directory.mkdirs()
            }

            // Nome único para o arquivo
            val fileName = "${prefix}_${UUID.randomUUID()}.jpg"
            val destFile = File(directory, fileName)

            // Salvar com compressão (80% de qualidade)
            val out = FileOutputStream(destFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
            out.flush()
            out.close()

            destFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Remove um arquivo do armazenamento interno.
     */
    fun deleteImageFromInternalStorage(path: String) {
        try {
            val file = File(path)
            if (file.exists()) {
                file.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
