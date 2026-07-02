package com.anotasmart.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
private val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))

fun formatDate(timestamp: Long): String {
    return dateFormat.format(Date(timestamp))
}

// função para formatação segura de Double
fun formatSafe(value: Double?): String {
    return value?.let { "%.2f".format(Locale("pt", "BR"), it) } ?: "0,00"
}
