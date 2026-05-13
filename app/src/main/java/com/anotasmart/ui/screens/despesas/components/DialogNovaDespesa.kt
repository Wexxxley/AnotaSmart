package com.anotasmart.ui.screens.despesas.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.anotasmart.model.entity.Category
import com.anotasmart.ui.components.GradeCategorias
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DialogNovaDespesa(
    categorias: List<Category>,
    onDismissRequest: () -> Unit,
    onConfirmar: (descricao: String, valor: Double, categoryId: String, data: Long) -> Unit
) {
    var descricao by remember { mutableStateOf("") }
    var valorText by remember { mutableStateOf("") }
    var categoryId by remember { mutableStateOf<String?>(null) }
    var dataSelecionada by remember { mutableStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dataSelecionada)
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR")) }

    val isConfirmEnabled = descricao.isNotBlank() && valorText.isNotBlank() && categoryId != null

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    dataSelecionada = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                    showDatePicker = false
                }) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                CenterAlignedTopAppBar(
                    title = { Text("Nova Despesa", style = MaterialTheme.typography.titleLarge) },
                    navigationIcon = {
                        IconButton(onClick = onDismissRequest) {
                            Icon(Icons.Default.Close, contentDescription = "Fechar")
                        }
                    }
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = descricao,
                        onValueChange = { descricao = it },
                        label = { Text("Descrição da despesa") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = valorText,
                        onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) valorText = it },
                        label = { Text("Valor (R$)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        prefix = { Text("R$ ") }
                    )

                    Text("Categoria", style = MaterialTheme.typography.labelLarge)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        categorias.forEach { categoria ->
                            val isSelected = categoryId == categoria.id
                            FilterChip(
                                selected = isSelected,
                                onClick = { categoryId = if (isSelected) null else categoria.id },
                                label = { Text(categoria.nome) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = dateFormatter.format(Date(dataSelecionada)),
                        onValueChange = { },
                        label = { Text("Data") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Selecionar Data")
                            }
                        }
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = {
                            onConfirmar(
                                descricao,
                                valorText.toDoubleOrNull() ?: 0.0,
                                categoryId!!,
                                dataSelecionada
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isConfirmEnabled
                    ) {
                        Text("Salvar")
                    }
                }
            }
        }
    }
}
