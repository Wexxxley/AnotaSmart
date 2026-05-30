package com.anotasmart.ui.screens.vendas.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogVendaParcelada(
    totalVenda: Double,
    onDismissRequest: () -> Unit,
    onConfirmar: (Int, Long) -> Unit
) {
    var numParcelas by remember { mutableIntStateOf(2) }
    var showDatePicker by remember { mutableStateOf(false) }
    
    // Data da primeira parcela (padrão: hoje + 30 dias)
    var dataSelecionada by remember {
        mutableLongStateOf(
            LocalDate.now().plusMonths(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = dataSelecionada
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        dataSelecionada = it
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("CANCELAR")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = "Venda Parcelada",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Seletor de Parcelas
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Quantidade de Parcelas",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = { if (numParcelas > 2) numParcelas-- },
                            enabled = numParcelas > 2
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = null)
                        }

                        Text(
                            text = "${numParcelas}x",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )

                        IconButton(
                            onClick = { if (numParcelas < 12) numParcelas++ },
                            enabled = numParcelas < 12
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                        }
                    }
                }

                // Seletor de Data
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Data da Primeira Parcela",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        val date = LocalDate.ofEpochDay(dataSelecionada / (24 * 60 * 60 * 1000L))
                        val formattedDate = Instant.ofEpochMilli(dataSelecionada)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                            .let { "${it.dayOfMonth.toString().padStart(2, '0')}/${it.monthValue.toString().padStart(2, '0')}/${it.year}" }
                        
                        Text(text = formattedDate)
                    }
                }

                // Resumo
                val valorParcela = totalVenda / numParcelas
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${numParcelas} parcelas de",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "R$ ${String.format("%.2f", valorParcela)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        },
        confirmButton = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { onConfirmar(numParcelas, dataSelecionada) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "CONFIRMAR VENDA",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    )
}
