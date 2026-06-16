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
    quantidadeNoCarrinho: Double = 0.0,
    onDismissRequest: () -> Unit,
    onConfirmar: (Double) -> Unit
) {
    val isService = produto.tipoItem == ItemType.SERVICO
    val estoqueTotal = produto.quantidadeEstoque
    val estoqueDisponivel = (estoqueTotal - quantidadeNoCarrinho).coerceAtLeast(0.0)

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
                        text = "Estoque: ${String.format("%.2f", estoqueTotal)} ${produto.unidadeMedida.name}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (quantidadeNoCarrinho > 0) {
                        Text(
                            text = "Já no carrinho: ${if (produto.unidadeMedida == com.anotasmart.model.UnitType.UN) quantidadeNoCarrinho.toInt() else String.format("%.2f", quantidadeNoCarrinho)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    Text(
                        text = "Disponível: ${if (produto.unidadeMedida == com.anotasmart.model.UnitType.UN) estoqueDisponivel.toInt() else String.format("%.2f", estoqueDisponivel)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (estoqueDisponivel > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 16.dp, top = 4.dp)
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
                        onClick = { 
                            if (quantidadeSelecionada > 1.0) {
                                quantidadeSelecionada -= 1.0 
                            } else {
                                quantidadeSelecionada = 0.0
                            }
                        },
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
                        onClick = { 
                            val proxima = quantidadeSelecionada + 1.0
                            quantidadeSelecionada = if (isService) proxima else proxima.coerceAtMost(estoqueDisponivel)
                        },
                        enabled = isService || quantidadeSelecionada < estoqueDisponivel
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Aumentar quantidade")
                    }
                }

                if (!isService && estoqueDisponivel <= 0) {
                    Text(
                        text = "Limite de estoque atingido",
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