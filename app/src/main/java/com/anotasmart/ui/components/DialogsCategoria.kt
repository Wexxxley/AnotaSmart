package com.anotasmart.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anotasmart.model.CategoryType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogNovaCategoria(
    tipoInicial: CategoryType,
    onDismissRequest: () -> Unit,
    onConfirmar: (nome: String, tipo: CategoryType) -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf(tipoInicial) }
    val maxChar = 20

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Nova Categoria") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Column {
                    OutlinedTextField(
                        value = nome,
                        onValueChange = { if (it.length <= maxChar) nome = it },
                        label = { Text("Nome da Categoria") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Text(
                        text = "${nome.length} / $maxChar",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.align(androidx.compose.ui.Alignment.End),
                        color = if (nome.length >= maxChar) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Column {
                    Text("Tipo", style = MaterialTheme.typography.labelLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = tipo == CategoryType.ITENS,
                            onClick = { tipo = CategoryType.ITENS },
                            label = { Text("Produtos/Serviços") }
                        )
                        FilterChip(
                            selected = tipo == CategoryType.DESPESAS,
                            onClick = { tipo = CategoryType.DESPESAS },
                            label = { Text("Despesas") }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmar(nome, tipo) },
                enabled = nome.isNotBlank()
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancelar")
            }
        }
    )
}
