package com.anotasmart.ui.screens.vendas.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.anotasmart.model.entity.Product

@Composable
fun DialogAdicionarCarrinho(
    produto: Product,
    onDismissRequest: () -> Unit,
    onConfirmar: (Double) -> Unit
) {
    // Estado local para armazenar a quantidade
    var quantidadeSelecionada by remember { mutableDoubleStateOf(1.0) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = produto.nome,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Estoque ${produto.quantidadeEstoque} ${produto.unidadeMedida.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { if (quantidadeSelecionada > 1.0) quantidadeSelecionada -= 1.0 },
                        enabled = quantidadeSelecionada > 1.0
                    ) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Diminuir quantidade")
                    }

                    Text(
                        text = quantidadeSelecionada.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )

                    IconButton(
                        onClick = { if (quantidadeSelecionada < produto.quantidadeEstoque) quantidadeSelecionada += 1.0 },
                        enabled = quantidadeSelecionada < produto.quantidadeEstoque
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Aumentar quantidade")
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
                    onClick = { onConfirmar(quantidadeSelecionada) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val valorTotal = produto.precoVenda * quantidadeSelecionada
                    Text(
                        text = "ADICIONAR R$ ${String.format("%.2f", valorTotal)}",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    )
}