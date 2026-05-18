package com.anotasmart.ui.screens.clientes

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.anotasmart.model.entity.Client
import com.anotasmart.ui.components.DoubleDeleteConfirmationDialog
import com.anotasmart.ui.components.StandardScreen
import com.anotasmart.ui.components.expandableGroup
import com.anotasmart.ui.screens.pedidos.EmptyListMessage
import com.anotasmart.ui.screens.pedidos.ParcelaCard
import com.anotasmart.ui.screens.pedidos.VendaCard
import com.anotasmart.ui.viewModels.ClientesViewModel
import com.anotasmart.utils.FormatUtils
import com.anotasmart.utils.PhoneUtils
import androidx.compose.foundation.lazy.items

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

        DoubleDeleteConfirmationDialog(
            showDialog = showDeleteDialog,
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
                CardClienteSuperior(
                    cliente = client,
                    onWhatsAppClick = {
                        val url = PhoneUtils.getWhatsAppLink(client.telefone, "Olá ${client.nome}!")
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    }
                )
            }

            // Tabs de Navegação
            item {
                TabRow(selectedTabIndex = tabIndex, modifier = Modifier.padding(top = 16.dp)) {
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
            }

            // Conteúdo das Tabs
            if (tabIndex == 0) {
                if (vendas.isEmpty()) {
                    item { EmptyListMessage("Nenhuma compra registrada.") }
                } else {
                    items(vendas, key = { it.sale.id }) { vendaWithRelations ->
                        VendaCard(vendaWithRelations)
                    }
                }
            } else {
                if (recebiveis.isEmpty()) {
                    item { EmptyListMessage("Nenhuma parcela pendente.") }
                } else {
                    val groupedRecebiveis = recebiveis.groupBy { it.second.id }
                    
                    groupedRecebiveis.forEach { (saleId, items) ->
                        val isExpanded = expandedStates[saleId] ?: true

                        expandableGroup(
                            title = "Pedido #${saleId.takeLast(4)}",
                            subtitle = "Total Pendente: ${FormatUtils.formatCurrency(items.sumOf { it.first.valor })}",
                            items = items,
                            isExpanded = isExpanded,
                            onToggle = { expandedStates[saleId] = !isExpanded },
                            key = { it.first.id }
                        ) { (installment, sale, client) ->
                            ParcelaCard(installment, sale, client, onBaixa = { viewModel.marcarComoPaga(it) })
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

@Composable
fun CardClienteSuperior(
    cliente: Client,
    onWhatsAppClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Row com Foto e Nome
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    if (!cliente.imagePath.isNullOrEmpty()) {
                        val imageModel: Any = cliente.imagePath.toIntOrNull() ?: cliente.imagePath
                        AsyncImage(
                            model = imageModel,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = cliente.nome,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Coluna com Telefone e Endereço
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                IconInfoRow(icon = Icons.Default.Phone, info = cliente.telefone)
                IconInfoRow(icon = Icons.Default.LocationOn, info = cliente.endereco ?: "Sem endereço cadastrado")
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botão WhatsApp
            Button(
                onClick = onWhatsAppClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)), // Verde WhatsApp
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Chat, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Abrir no WhatsApp", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun IconInfoRow(icon: ImageVector, info: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = info,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
