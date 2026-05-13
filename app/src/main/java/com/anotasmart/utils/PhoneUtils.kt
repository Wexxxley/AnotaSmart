package com.anotasmart.utils

import android.net.Uri
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class PhoneVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        // Remove tudo que não for dígito
        val cleanText = text.text.filter { it.isDigit() }
        
        val out = StringBuilder()
        val mask = if (cleanText.length <= 10) "(##) ####-####" else "(##) #####-####"
        
        var i = 0
        mask.forEach { char ->
            if (char == '#') {
                if (i < cleanText.length) {
                    out.append(cleanText[i])
                    i++
                }
            } else if (i < cleanText.length) {
                out.append(char)
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                var transformedOffset = 0
                var originalCharCount = 0
                
                for (char in out.indices) {
                    if (originalCharCount >= offset) break
                    transformedOffset++
                    if (out[char].isDigit()) {
                        originalCharCount++
                    }
                }
                return transformedOffset
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 0) return 0
                var originalOffset = 0
                for (i in 0 until offset.coerceAtMost(out.length)) {
                    if (out[i].isDigit()) {
                        originalOffset++
                    }
                }
                return originalOffset
            }
        }

        return TransformedText(AnnotatedString(out.toString()), offsetMapping)
    }
}

object PhoneUtils {
    fun formatPhone(phone: String): String {
        val clean = phone.filter { it.isDigit() }
        return when (clean.length) {
            11 -> "(${clean.substring(0, 2)}) ${clean.substring(2, 7)}-${clean.substring(7)}"
            10 -> "(${clean.substring(0, 2)}) ${clean.substring(2, 6)}-${clean.substring(6)}"
            else -> phone
        }
    }

    /**
     * Remove todos os caracteres não numéricos.
     */
    fun cleanPhoneNumber(phone: String): String {
        return phone.filter { it.isDigit() }
    }

    /**
     * Gera o link do WhatsApp para o número fornecido.
     * Adiciona o DDI +55 (Brasil) caso não exista.
     */
    fun getWhatsAppLink(phone: String, message: String = ""): String {
        val cleanPhone = cleanPhoneNumber(phone)
        val phoneWithDDI = if (cleanPhone.length <= 11) "55$cleanPhone" else cleanPhone
        return "https://api.whatsapp.com/send?phone=$phoneWithDDI&text=${Uri.encode(message)}"
    }
}
