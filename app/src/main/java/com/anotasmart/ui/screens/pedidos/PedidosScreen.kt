package com.anotasmart.ui.screens.pedidos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anotasmart.model.InstallmentStatus
import com.anotasmart.model.SaleStatus
import com.anotasmart.model.SaleWithRelations
import com.anotasmart.model.entity.Client
import com.anotasmart.model.entity.Installment
import com.anotasmart.model.entity.Sale
import com.anotasmart.ui.viewModels.PedidosViewModel
import com.anotasmart.utils.FormatUtils

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

        TabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = tabIndex == index,
                    onClick = { tabIndex = index },
                    text = { Text(title) },
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
fun BannerResumo(totalAReceber: Double, totalAtrasado: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "A Receber",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = FormatUtils.formatCurrency(totalAReceber),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Em Atraso",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = FormatUtils.formatCurrency(totalAtrasado),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun HistoricoVendasList(vendas: List<SaleWithRelations>) {
    if (vendas.isEmpty()) {
        EmptyListMessage("Nenhuma venda registrada.")
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
    recebiveis: List<Triple<Installment, Sale, Client?>>,
    onBaixa: (String) -> Unit
) {
    if (recebiveis.isEmpty()) {
        EmptyListMessage("Nenhuma parcela pendente.")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(recebiveis) { (installment, sale, client) ->
                ParcelaCard(installment, sale, client, onBaixa)
            }
        }
    }
}

@Composable
fun VendaCard(vendaWithRelations: SaleWithRelations) {
    val venda = vendaWithRelations.sale
    val client = vendaWithRelations.client
    val installments = vendaWithRelations.installments

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp, 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = client?.nome ?: "Cliente Avulso",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = FormatUtils.formatDate(venda.dataVenda),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                Text(
                    text = FormatUtils.formatCurrency(venda.valorTotal),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            val isParcelado = installments.size > 1 || 
                             (installments.size == 1 && installments.first().metodoPagamento == com.anotasmart.model.PaymentMethod.PARCELADO)

            val pagas = installments.count { it.statusParcela == InstallmentStatus.PAGA }
            val total = installments.size
            val progresso = if (total > 0) pagas.toFloat() / total else 0f
            val finalizada = total > 0 && pagas == total

            if (isParcelado && !finalizada) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Progresso: $pagas/$total pagas",
                            fontSize = 12.sp
                        )
                        if (installments.any { it.statusParcela == InstallmentStatus.ATRASADA || (it.statusParcela == InstallmentStatus.PENDENTE && it.dataVencimento < System.currentTimeMillis()) }) {
                            Text(
                                text = "ATRASADA",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    LinearProgressIndicator(
                        progress = { progresso },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .padding(top = 4.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            } else {
                val label = when {
                    isParcelado -> "PARCELADO"
                    installments.isNotEmpty() -> installments.first().metodoPagamento?.name ?: "À VISTA"
                    else -> venda.status.name
                }

                SuggestionChip(
                    onClick = { },
                    label = { Text(label, fontSize = 10.sp) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = Color(0xFFE8F5E9),
                        labelColor = Color(0xFF2E7D32)
                    ),
                    border = null
                )
            }
        }
    }
}

@Composable
fun ParcelaCard(
    installment: Installment,
    sale: Sale,
    client: Client?,
    onBaixa: (String) -> Unit
) {
    val hoje = System.currentTimeMillis()
    val estaAtrasada = installment.dataVencimento < hoje
    val backgroundColor = if (estaAtrasada) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.surface

    var showConfirmDialog by remember { mutableStateOf(false) }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    onBaixa(installment.id)
                    showConfirmDialog = false
                }) {
                    Text("CONFIRMAR")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("CANCELAR")
                }
            },
            title = { Text("Confirmar Pagamento") },
            text = { 
                Text("Deseja registrar o pagamento da parcela ${installment.numeroParcela} no valor de ${FormatUtils.formatCurrency(installment.valor)}?")
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = client?.nome ?: "Cliente Avulso",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    if (estaAtrasada) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Atrasada",
                            tint = Color.Red,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(
                    text = "Parcela ${installment.numeroParcela} - Vence em ${FormatUtils.formatDate(installment.dataVencimento)}",
                    fontSize = 12.sp,
                    color = if (estaAtrasada) Color.Red else Color.Gray
                )
                Text(
                    text = FormatUtils.formatCurrency(installment.valor),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = if (estaAtrasada) Color.Red else MaterialTheme.colorScheme.primary
                )
            }

            IconButton(
                onClick = { showConfirmDialog = true },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = "Dar Baixa")
            }
        }
    }
}

@Composable
fun EmptyListMessage(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = message, color = Color.Gray)
    }
}
