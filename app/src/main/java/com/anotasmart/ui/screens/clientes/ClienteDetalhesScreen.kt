package com.anotasmart.ui.screens.clientes

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.anotasmart.model.entity.Client
import com.anotasmart.ui.components.AppTabRow
import com.anotasmart.ui.components.DoubleDeleteConfirmationDialog
import com.anotasmart.ui.components.StandardScreen
import com.anotasmart.ui.components.expandableGroup
import com.anotasmart.ui.components.EmptyListMessagePedidos
import com.anotasmart.ui.viewModels.ClientesViewModel
import com.anotasmart.utils.PhoneUtils
import androidx.compose.foundation.lazy.items
import com.anotasmart.ui.components.ParcelaCard
import com.anotasmart.ui.components.VendaCard
import com.anotasmart.utils.formatSafe
import androidx.core.net.toUri
import com.anotasmart.ui.screens.clientes.components.CardClient

@Composable
fun ClienteDetalhesScreen(
    clientId: String?,
    viewModel: ClientesViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var cliente by remember { mutableStateOf<Client?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var tabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Histórico", "Contas a Receber")
    val vendas by viewModel.vendasCliente.collectAsState()
    val recebiveis by viewModel.recebiveisCliente.collectAsState()
    val expandedStates = remember { mutableStateMapOf<String, Boolean>() }

    LaunchedEffect(clientId) {
        clientId?.let {
            cliente = viewModel.getClientById(it)
            viewModel.setSelectedClient(it)
        }
    }

    if (cliente != null) {
        val client = cliente!!

        if (showDeleteDialog) {
            DoubleDeleteConfirmationDialog(
                showDialog = true,
                onDismissRequest = { showDeleteDialog = false },
                onConfirm = {
                    viewModel.deletarCliente(client) {
                        onBackClick()
                    }
                },
                title = "Apagar Cliente",
                message1 = "Você tem certeza que deseja apagar o cliente ${client.nome}?",
                message2 = "Esta ação não pode ser desfeita. Todos os dados de ${client.nome} serão removidos permanentemente. Confirmar?"
            )
        }

        StandardScreen(
            title = "Detalhes do Cliente",
            onBackClick = onBackClick,
            headerActions = {
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Apagar Cliente",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        ) {
            // Card Superior
            item {
                CardClient(
                    cliente = client,
                    onWhatsAppClick = {
                        val url = PhoneUtils.getWhatsAppLink(client.telefone, "Olá ${client.nome}!")
                        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                        context.startActivity(intent)
                        // Intent é um "pedido" que você faz ao SO. Intent.ACTION_VIEW Diz ao Android que você quer visualizar algo (uma URL).
                    }
                )
            }

            // Tabs de Navegação
            item {
                AppTabRow(
                    tabIndex = tabIndex,
                    tabs = tabs,
                    icons = listOf(Icons.Default.History, Icons.Default.Payments),
                    onTabSelected = { tabIndex = it },
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            // Conteúdo das Tabs
            if (tabIndex == 0) {
                if (vendas.isEmpty()) {
                    item { EmptyListMessagePedidos("Nenhuma compra registrada.") }
                } else {
                    items(vendas, key = { it.sale.id }) { vendaWithRelations ->
                        VendaCard(vendaWithRelations)
                    }
                }
            } else {
                if (recebiveis.isEmpty()) {
                    item { EmptyListMessagePedidos("Nenhuma parcela pendente.") }
                } else {
                    val groupedRecebiveis = recebiveis.groupBy { it.second.sale.id }
                    
                    groupedRecebiveis.forEach { (saleId, items) ->
                        val isExpanded = expandedStates[saleId] ?: true

                        expandableGroup(
                            title = "Pedido #${saleId.takeLast(4)}",
                            subtitle = "Total Pendente: R$ ${formatSafe(items.sumOf { it.first.valor })}",
                            items = items,
                            isExpanded = isExpanded,
                            onToggle = { expandedStates[saleId] = !isExpanded },
                            key = { it.first.id }
                        ) { (installment, saleWithRelations, client) ->
                            ParcelaCard(installment, saleWithRelations, client, onBaixa = { viewModel.marcarComoPaga(it) })
                        }
                    }
                }
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Cliente não encontrado")
        }
    }
}
