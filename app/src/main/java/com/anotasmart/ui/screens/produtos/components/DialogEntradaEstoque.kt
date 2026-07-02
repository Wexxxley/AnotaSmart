package com.anotasmart.ui.screens.produtos.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.anotasmart.model.entity.Product
import com.anotasmart.utils.formatSafe

@Composable
fun DialogEntradaEstoque(
    produto: Product,
    onDismissRequest: () -> Unit,
    onConfirmar: (quantidade: Double, novoPrecoCusto: Double) -> Unit
) {
    var quantidadeText by remember { mutableStateOf("") }
    var precoCustoText by remember { mutableStateOf(produto.precoCusto.toString()) }

    val quantidade = quantidadeText.toDoubleOrNull() ?: 0.0
    val precoCusto = precoCustoText.toDoubleOrNull() ?: 0.0

    val isConfirmEnabled = quantidade > 0 && precoCusto >= 0

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = "Entrada de Estoque", style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = produto.nome,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = quantidadeText,
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() || it == '.' }) {
                            quantidadeText = newValue
                        }
                    },
                    label = { Text("Quantidade recebida") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    suffix = { Text(produto.unidadeMedida.name) },
                    isError = quantidadeText.isNotEmpty() && quantidade <= 0,
                    singleLine = true
                )

                OutlinedTextField(
                    value = precoCustoText,
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() || it == '.' }) {
                            precoCustoText = newValue
                        }
                    },
                    label = { Text("Novo preço de custo unitário") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    prefix = { Text("R$ ") },
                    isError = precoCustoText.isNotEmpty() && precoCusto < 0,
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Custo médio atual:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "R$ ${formatSafe(produto.precoCusto)}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmar(quantidade, precoCusto) },
                enabled = isConfirmEnabled
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancelar")
            }
        }
    )
}
