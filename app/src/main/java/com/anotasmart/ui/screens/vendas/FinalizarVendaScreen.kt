package com.anotasmart.ui.screens.vendas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.anotasmart.AnotaSmartApplication
import com.anotasmart.model.PaymentMethod
import com.anotasmart.ui.components.StandardScreen
import com.anotasmart.ui.screens.vendas.components.DialogConfirmacaoVenda
import com.anotasmart.ui.screens.vendas.components.DialogVendaParcelada
import com.anotasmart.ui.viewModels.FinalizarVendaViewModel
import com.anotasmart.ui.viewModels.FinalizarVendaViewModelFactory

@Composable
fun FinalizarVendaScreen(
    onBackClick: () -> Unit,
    onConfirmarVenda: () -> Unit // Este parâmetro pode ser renomeado ou usado para navegação após sucesso
) {
    val context = LocalContext.current
    val database = (context.applicationContext as AnotaSmartApplication).database
    val viewModel: FinalizarVendaViewModel = viewModel(
        factory = FinalizarVendaViewModelFactory(
            database.clientDao(),
            database.cartItemDao(),
            database.saleDao(),
            database.productDao()
        )
    )

    val clientes by viewModel.clientesFiltrados.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val clienteSelecionado by viewModel.clienteSelecionado.collectAsState()
    val metodoPagamento by viewModel.metodoPagamento.collectAsState()
    val totalVenda by viewModel.totalVenda.collectAsState()

    var showConfirmDialog by remember { mutableStateOf(false) }
    var showInstallmentDialog by remember { mutableStateOf(false) }

    if (showConfirmDialog) {
        DialogConfirmacaoVenda(
            totalVenda = totalVenda,
            onDismissRequest = { showConfirmDialog = false },
            onConfirmar = {
                viewModel.confirmarVendaAVista {
                    showConfirmDialog = false
                    onConfirmarVenda()
                }
            }
        )
    }

    if (showInstallmentDialog) {
        DialogVendaParcelada(
            totalVenda = totalVenda,
            onDismissRequest = { showInstallmentDialog = false },
            onConfirmar = { numParcelas, dataPrimeira ->
                viewModel.confirmarVendaParcelada(numParcelas, dataPrimeira) {
                    showInstallmentDialog = false
                    onConfirmarVenda()
                }
            }
        )
    }

    val isParceladoSemCliente = metodoPagamento == PaymentMethod.PARCELADO && clienteSelecionado == null
    val isConfirmEnabled = metodoPagamento != null && !isParceladoSemCliente

    StandardScreen(
        title = "Finalizar Venda",
        onBackClick = onBackClick,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp)
                ) {
                    if (isParceladoSemCliente) {
                        Text(
                            text = "Selecione um cliente para vendas parceladas",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(bottom = 8.dp).align(Alignment.CenterHorizontally)
                        )
                    }
                    Button(
                        onClick = {
                            if (metodoPagamento == PaymentMethod.DINHEIRO || metodoPagamento == PaymentMethod.PIX) {
                                showConfirmDialog = true
                            } else if (metodoPagamento == PaymentMethod.PARCELADO) {
                                showInstallmentDialog = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isConfirmEnabled
                    ) {
                        Text("CONFIRMAR VENDA")
                    }
                }
            }
        }
    ) {
        // Seção Cliente
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Selecionar Cliente",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                com.anotasmart.ui.components.BarraBusca(
                    query = searchQuery,
                    onQueryChange = viewModel::onSearchQueryChanged,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    item {
                        ClienteItem(
                            nome = "Nenhum",
                            imagePath = null,
                            isSelected = clienteSelecionado == null,
                            onClick = { viewModel.selecionarCliente(null) }
                        )
                    }
                    items(clientes) { cliente ->
                        ClienteItem(
                            nome = cliente.nome,
                            imagePath = cliente.imagePath,
                            isSelected = clienteSelecionado?.id == cliente.id,
                            onClick = { viewModel.selecionarCliente(cliente) }
                        )
                    }
                }
            }
        }

        // Seção Forma de Pagamento
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Forma de Pagamento",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                PaymentMethodButton(
                    title = "Dinheiro",
                    isSelected = metodoPagamento == PaymentMethod.DINHEIRO,
                    onClick = { viewModel.selecionarMetodoPagamento(PaymentMethod.DINHEIRO) }
                )

                PaymentMethodButton(
                    title = "Pix",
                    isSelected = metodoPagamento == PaymentMethod.PIX,
                    onClick = { viewModel.selecionarMetodoPagamento(PaymentMethod.PIX) }
                )

                PaymentMethodButton(
                    title = "Parcelado (Venda a Prazo)",
                    isSelected = metodoPagamento == PaymentMethod.PARCELADO,
                    onClick = { viewModel.selecionarMetodoPagamento(PaymentMethod.PARCELADO) }
                )
            }
        }
    }
}

@Composable
fun ClienteItem(
    nome: String,
    imagePath: String?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(64.dp) // Aumentado levemente para acomodar a borda
                .clip(CircleShape)
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant
                )
                .then(
                    if (isSelected) Modifier.border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            if (!imagePath.isNullOrEmpty()) {
                val imageModel: Any = imagePath.toIntOrNull() ?: imagePath
                AsyncImage(
                    model = imageModel,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(if (isSelected) 3.dp else 0.dp) // Espaço para a borda não cortar a foto
                        .clip(CircleShape)
                )
            } else {
                Icon(
                    imageVector = if (nome == "Nenhum") Icons.Default.PersonAdd else Icons.Default.Person,
                    contentDescription = null,
                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Text(
            text = nome,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal,
            maxLines = 1,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun PaymentMethodButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 0.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
