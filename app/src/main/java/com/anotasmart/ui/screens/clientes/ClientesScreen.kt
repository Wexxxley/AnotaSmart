package com.anotasmart.ui.screens.clientes

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.anotasmart.AnotaSmartApplication
import com.anotasmart.model.ImageDirectory
import com.anotasmart.model.entity.Client
import com.anotasmart.ui.components.BarraBusca
import com.anotasmart.ui.screens.clientes.components.DialogNovoCliente
import com.anotasmart.ui.viewModels.ClientesViewModel
import com.anotasmart.ui.viewModels.ClientesViewModelFactory
import com.anotasmart.utils.ImageUtils
import com.anotasmart.utils.PhoneUtils
import androidx.core.net.toUri

@Composable
fun ClientesScreen(
    onClientClick: (Client) -> Unit = {}
) {
    val context = LocalContext.current
    val database = (context.applicationContext as AnotaSmartApplication).database
    val viewModel: ClientesViewModel = viewModel(
        factory = ClientesViewModelFactory(
            database.clientDao(),
            database.saleDao(),
            database.installmentDao()
        )
    )
    val clientes by viewModel.clientesFiltrados.collectAsState(initial = emptyList())
    val searchQuery by viewModel.searchQuery.collectAsState()
    val mostrarModalNovoCliente by viewModel.mostrarModalNovoCliente.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            BarraBusca(
                query = searchQuery,
                onQueryChange = viewModel::onSearchQueryChanged
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(clientes) { cliente ->
                    ClientItem(
                        cliente = cliente,
                        onClick = { onClientClick(cliente) }
                    )
                }
            }
        }

        ExtendedFloatingActionButton(
            onClick = { viewModel.abrirModalNovoCliente() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            icon = { Icon(Icons.Default.Add, contentDescription = null) },
            text = { Text("Cadastrar cliente") }
        )

        if (mostrarModalNovoCliente) {
            DialogNovoCliente(
                onDismissRequest = { viewModel.fecharModalNovoCliente() },
                onConfirmar = { nome, telefone, endereco, imageUriString ->
                    val internalImagePath = imageUriString?.let {
                        ImageUtils.saveImageToInternalStorage(context,
                            it.toUri(), ImageDirectory.CLIENTS)
                    }
                    viewModel.salvarNovoCliente(nome, telefone, endereco, internalImagePath)
                }
            )
        }
    }
}

@Composable
fun ClientItem(
    cliente: Client,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (cliente.imagePath != null) {
                AsyncImage(
                    model = cliente.imagePath,
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
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
                    text = PhoneUtils.formatPhone(cliente.telefone),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
