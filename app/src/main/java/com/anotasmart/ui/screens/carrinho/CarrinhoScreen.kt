package com.anotasmart.ui.screens.carrinho

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anotasmart.utils.formatSafe
import com.anotasmart.ui.components.StandardScreen
import com.anotasmart.ui.screens.carrinho.componentes.CartItemCard
import com.anotasmart.ui.viewModels.CartViewModel

@Composable
fun CarrinhoScreen(
    viewModel: CartViewModel,
    onBackClick: () -> Unit,
    onFinalizarVenda: () -> Unit
) {
    val items by viewModel.items.collectAsState() // converte flow em stateflow
    val totalValor by viewModel.totalValor.collectAsState()

    StandardScreen(
        title = "Carrinho",
        onBackClick = onBackClick,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        bottomBar = {
            if (items.isNotEmpty()) {
                Surface(
                    tonalElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Total", style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = "R$ ${formatSafe(totalValor)}",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Button(
                            onClick = onFinalizarVenda,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                        ) {
                            Text("FINALIZAR VENDA")
                        }
                    }
                }
            }
        }
    ) {
        if (items.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Seu carrinho está vazio",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(items) { item ->
                CartItemCard(
                    item = item,
                    onIncrement = { viewModel.alterarQuantidade(item.id, item.quantidade + 1) },
                    onDecrement = { viewModel.alterarQuantidade(item.id, item.quantidade - 1) },
                    onDelete = { viewModel.removerItem(item.id) }
                )
            }
        }
    }
}

