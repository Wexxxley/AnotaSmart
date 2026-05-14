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
import com.anotasmart.model.ItemType
import com.anotasmart.model.entity.Product

@Composable
fun DialogAdicionarCarrinho(
    produto: Product,
    onDismissRequest: () -> Unit,
    onConfirmar: (Double) -> Unit
) {
    val isService = produto.tipoItem == ItemType.SERVICO
    val estoqueDisponivel = produto.quantidadeEstoque

    // Estado local para armazenar a quantidade, inicializado corretamente
    var quantidadeSelecionada by remember(produto.id) {
        val inicial = when {
            isService -> 1.0
            estoqueDisponivel >= 1.0 -> 1.0
            estoqueDisponivel > 0.0 -> estoqueDisponivel
            else -> 0.0
        }
        mutableDoubleStateOf(inicial)
    }

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
                if (!isService) {
                    Text(
                        text = "Estoque ${produto.quantidadeEstoque} ${produto.unidadeMedida.name}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (estoqueDisponivel > 0) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                } else {
                    Text(
                        text = "Serviço",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { if (quantidadeSelecionada > 1.0) quantidadeSelecionada -= 1.0 else if (quantidadeSelecionada > 0 && !isService) quantidadeSelecionada = 0.0 },
                        enabled = quantidadeSelecionada > 0
                    ) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Diminuir quantidade")
                    }

                    Text(
                        text = if (produto.unidadeMedida == com.anotasmart.model.UnitType.UN) 
                                   quantidadeSelecionada.toInt().toString() 
                               else String.format("%.2f", quantidadeSelecionada),
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )

                    IconButton(
                        onClick = { quantidadeSelecionada += 1.0 },
                        enabled = isService || quantidadeSelecionada < estoqueDisponivel
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Aumentar quantidade")
                    }
                }
                
                if (!isService && estoqueDisponivel <= 0) {
                    Text(
                        text = "Produto sem estoque",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
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
                    modifier = Modifier.fillMaxWidth(),
                    enabled = quantidadeSelecionada > 0 && (isService || quantidadeSelecionada <= estoqueDisponivel)
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