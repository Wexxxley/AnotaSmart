package com.anotasmart.utils

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

object PixUtils {

    fun generatePixPayload(key: String, merchantName: String, merchantCity: String): String {
        val payload = StringBuilder()
        
        // Payload Indicator
        payload.append("000201")
        
        // Merchant Account Information
        val gui = "0014BR.GOV.BCB.PIX"
        val keyPart = "01${key.length.toString().padStart(2, '0')}$key"
        val merchantAccountInfo = "$gui$keyPart"
        payload.append("26${merchantAccountInfo.length.toString().padStart(2, '0')}$merchantAccountInfo")
        
        // Merchant Category Code
        payload.append("52040000")
        
        // Transaction Currency (986 = BRL)
        payload.append("5303986")
        
        // Country Code
        payload.append("5802BR")
        
        // Merchant Name
        val name = if (merchantName.length > 25) merchantName.substring(0, 25) else merchantName
        payload.append("59${name.length.toString().padStart(2, '0')}$name")
        
        // Merchant City
        val city = if (merchantCity.length > 15) merchantCity.substring(0, 15) else merchantCity
        payload.append("60${city.length.toString().padStart(2, '0')}$city")
        
        // Additional Data Field
        payload.append("62070503***")
        
        // CRC16
        payload.append("6304")
        val crc = calculateCRC16(payload.toString())
        payload.append(crc)
        
        return payload.toString()
    }

    private fun calculateCRC16(payload: String): String {
        var crc = 0xFFFF
        val polynomial = 0x1021

        for (b in payload.toByteArray()) {
            for (i in 0..7) {
                val bit = (b.toInt() shr (7 - i) and 1) == 1
                val c15 = (crc shr 15 and 1) == 1
                crc = crc shl 1
                if (c15 xor bit) crc = crc xor polynomial
            }
        }

        crc = crc and 0xFFFF
        return Integer.toHexString(crc).uppercase().padStart(4, '0')
    }

    fun generateQRCode(content: String, size: Int = 512): Bitmap? {
        return try {
            val bitMatrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size)
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
            for (x in 0 until size) {
                for (y in 0 until size) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            bitmap
        } catch (e: Exception) {
            null
        }
    }
}
