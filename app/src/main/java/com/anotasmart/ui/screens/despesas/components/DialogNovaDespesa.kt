package com.anotasmart.ui.screens.despesas.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anotasmart.model.CategoryType
import com.anotasmart.model.entity.Category
import com.anotasmart.ui.components.CampoMoeda
import com.anotasmart.ui.components.DialogNovaCategoria
import com.anotasmart.ui.components.FullScreenDialog
import com.anotasmart.ui.components.GradeCategorias
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogNovaDespesa(
    categorias: List<Category>,
    onDismissRequest: () -> Unit,
    onConfirmar: (descricao: String, valor: Double, categoryId: String, data: Long) -> Unit,
    onNovaCategoria: (String, CategoryType) -> Unit
) {
    var descricao by remember { mutableStateOf("") }
    var valorText by remember { mutableStateOf("") }
    var categoryId by remember { mutableStateOf<String?>(null) }
    var dataSelecionada by remember { mutableStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showNovaCategoriaDialog by remember { mutableStateOf(false) }

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

    if (showNovaCategoriaDialog) {
        DialogNovaCategoria(
            tipoInicial = CategoryType.DESPESAS,
            onDismissRequest = { showNovaCategoriaDialog = false },
            onConfirmar = { nomeCat, tipo ->
                onNovaCategoria(nomeCat, tipo)
                showNovaCategoriaDialog = false
            }
        )
    }

    FullScreenDialog(
        title = "Nova Despesa",
        onDismissRequest = onDismissRequest,
        confirmButtonText = "Salvar",
        isConfirmEnabled = isConfirmEnabled,
        onConfirmClick = {
            onConfirmar(
                descricao,
                valorText.toDoubleOrNull() ?: 0.0,
                categoryId!!,
                dataSelecionada
            )
        }
    ) {
        OutlinedTextField(
            value = descricao,
            onValueChange = { descricao = it },
            label = { Text("Descrição da despesa") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        CampoMoeda(
            value = valorText,
            onValueChange = { valorText = it },
            label = "Valor",
            modifier = Modifier.fillMaxWidth()
        )

        Text("Categoria", style = MaterialTheme.typography.labelLarge)
        GradeCategorias(
            categorias = categorias,
            selectedCategoryId = categoryId,
            onCategorySelected = { categoryId = it },
            onAddCategoryClick = { showNovaCategoriaDialog = true }
        )

        OutlinedTextField(
            value = dateFormatter.format(Date(dataSelecionada)),
            onValueChange = { },
            label = { Text("Data") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Selecionar Data")
                }
            }
        )
    }
}
