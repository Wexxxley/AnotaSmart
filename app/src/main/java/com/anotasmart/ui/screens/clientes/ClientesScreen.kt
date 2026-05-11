package com.anotasmart.ui.screens.clientes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.anotasmart.model.entity.Client
import com.anotasmart.ui.components.BarraBusca
import com.anotasmart.ui.screens.clientes.components.DialogNovoCliente
import com.anotasmart.ui.viewModels.ClientesViewModel

@Composable
fun ClientesScreen(
    viewModel: ClientesViewModel,
    onClientClick: (Client) -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val clientes by viewModel.clientesFiltrados.collectAsState(initial = emptyList())
    val mostrarModalNovoCliente by viewModel.mostrarModalNovoCliente.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            BarraBusca(
                query = searchQuery,
                onQueryChange = { viewModel.onSearchQueryChanged(it) }
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 80.dp) // Espaço para o FAB
            ) {
                items(clientes) { cliente ->
                    ItemCliente(
                        cliente = cliente,
                        onClick = { onClientClick(cliente) }
                    )
                }
            }
        }

        // FAB para adicionar cliente
        FloatingActionButton(
            onClick = { viewModel.abrirModalNovoCliente() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Adicionar Cliente")
        }

        // Modal para novo cliente
        if (mostrarModalNovoCliente) {
            DialogNovoCliente(
                onDismissRequest = { viewModel.fecharModalNovoCliente() },
                onConfirmar = { nome, telefone, endereco, imagePath ->
                    viewModel.salvarNovoCliente(nome, telefone, endereco, imagePath)
                }
            )
        }
    }
}

@Composable
fun ItemCliente(
    cliente: Client,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagem do Cliente
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .padding(2.dp),
                contentAlignment = Alignment.Center
            ) {
                if (!cliente.imagePath.isNullOrEmpty()) {
                    val imageModel: Any = cliente.imagePath.toIntOrNull() ?: cliente.imagePath
                    AsyncImage(
                        model = imageModel,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(CircleShape)
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

            Column {
                Text(
                    text = cliente.nome,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = cliente.telefone,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
