package com.anotasmart.ui.screens.pedidos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anotasmart.model.SaleWithRelations
import com.anotasmart.model.entity.Client
import com.anotasmart.model.entity.Installment
import com.anotasmart.ui.screens.pedidos.components.BannerResumo
import com.anotasmart.ui.components.EmptyListMessagePedidos
import com.anotasmart.ui.components.ParcelaCard
import com.anotasmart.ui.components.VendaCard
import com.anotasmart.ui.components.expandableGroup
import com.anotasmart.ui.viewModels.PedidosViewModel
import com.anotasmart.utils.formatSafe

@Composable
fun PedidosScreen(
    viewModel: PedidosViewModel
) {
    var tabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Histórico", "Contas a Receber")

    val vendas by viewModel.vendas.collectAsState()
    val recebiveis by viewModel.recebiveis.collectAsState()
    val totalAReceber by viewModel.totalAReceber.collectAsState()
    val totalAtrasado by viewModel.totalAtrasado.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        BannerResumo(totalAReceber, totalAtrasado)

        PrimaryTabRow(
            selectedTabIndex = tabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = tabIndex == index,
                    onClick = { tabIndex = index },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = if (index == 0) Icons.Default.History else Icons.Default.Payments,
                            contentDescription = null
                        )
                    }
                )
            }
        }

        when (tabIndex) {
            0 -> HistoricoVendasList(vendas)
            1 -> ContasAReceberList(recebiveis, onBaixa = { viewModel.marcarComoPaga(it) })
        }
    }
}

@Composable
fun HistoricoVendasList(vendas: List<SaleWithRelations>) {
    if (vendas.isEmpty()) {
        EmptyListMessagePedidos("Nenhuma venda registrada.")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(vendas) { vendaWithRelations ->
                VendaCard(vendaWithRelations)
            }
        }
    }
}

@Composable
fun ContasAReceberList(
    recebiveis: List<Triple<Installment, SaleWithRelations, Client?>>,
    onBaixa: (String) -> Unit
) {
    if (recebiveis.isEmpty()) {
        EmptyListMessagePedidos("Nenhuma parcela pendente.")
    } else {
        // Agrupar por Sale
        val groupedRecebiveis = remember(recebiveis) {
            recebiveis.groupBy { it.second.sale.id }
        }
        val expandedStates = remember { mutableStateMapOf<String, Boolean>() }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            groupedRecebiveis.forEach { (saleId, items) ->
                val firstItem = items.first()
                val client = firstItem.third
                val isExpanded = expandedStates[saleId] ?: true

                expandableGroup(
                    title = client?.nome ?: "Cliente Avulso",
                    subtitle = "Total Pendente: R$ ${formatSafe(items.sumOf { it.first.valor })}",
                    items = items,
                    isExpanded = isExpanded,
                    onToggle = { expandedStates[saleId] = !isExpanded },
                    key = { it.first.id }
                ) { (installment, saleWithRelations, client) ->
                    ParcelaCard(installment, saleWithRelations, client, onBaixa)
                }
            }
        }
    }
}
