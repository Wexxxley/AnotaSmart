package com.anotasmart.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.anotasmart.model.ImageDirectory
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

import android.graphics.Color
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropActivity

object ImageUtils {
    /**
     * Configura o uCrop para recorte.
     */
    fun startUCrop(context: Context, sourceUri: Uri, destinationUri: Uri, isCircular: Boolean = true): UCrop {
        val options = UCrop.Options().apply {
            if (isCircular) {
                setCircleDimmedLayer(true) // Máscara circular
            }
            setShowCropFrame(!isCircular)    // Mostrar moldura quadrada se não for circular
            setShowCropGrid(!isCircular)     // Mostrar grade se não for circular
            setCompressionFormat(Bitmap.CompressFormat.JPEG)
            setCompressionQuality(80)
            setHideBottomControls(false)
            setFreeStyleCropEnabled(false)
            setAllowedGestures(UCropActivity.ALL, UCropActivity.ALL, UCropActivity.ALL)
        }

        return UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(1f, 1f) // Quadrado
            .withOptions(options)
    }

    /**
     * Copia uma imagem de uma URI para o armazenamento interno do app,
     * aplicando compressão para economizar espaço.
     * Retorna o caminho absoluto do novo arquivo ou null em caso de erro.
     */
    fun saveImageToInternalStorage(context: Context, uri: Uri, directory: ImageDirectory): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            // Criar diretório se não existir
            val dir = File(context.filesDir, directory.folderName)
            if (!dir.exists()) {
                dir.mkdirs()
            }

            // Nome único para o arquivo
            val fileName = "${directory.prefix}_${UUID.randomUUID()}.jpg"
            val destFile = File(dir, fileName)

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
