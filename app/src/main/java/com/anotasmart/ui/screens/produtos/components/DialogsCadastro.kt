package com.anotasmart.ui.screens.produtos.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.anotasmart.model.ItemType
import com.anotasmart.model.UnitType
import com.anotasmart.model.entity.Category
import com.anotasmart.model.entity.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogEditarItem(
    produto: Product,
    categorias: List<Category>,
    onDismissRequest: () -> Unit,
    onConfirmar: (
        id: String,
        nome: String,
        categoryId: String?,
        precoVenda: Double,
        precoCusto: Double,
        unidadeMedida: UnitType,
        imagePath: String?,
        tipoItem: ItemType,
        quantidadeEstoque: Double
    ) -> Unit
) {
    var nome by remember { mutableStateOf(produto.nome) }
    var categoryId by remember { mutableStateOf<String?>(produto.categoryId) }
    var precoVendaText by remember { mutableStateOf(produto.precoVenda.toString()) }
    var precoCustoText by remember { mutableStateOf(produto.precoCusto.toString()) }
    var unidadeMedida by remember { mutableStateOf(produto.unidadeMedida) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(produto.imagePath?.let { Uri.parse(it) }) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> if (uri != null) selectedImageUri = uri }
    )

    val isConfirmEnabled = nome.isNotBlank() && precoVendaText.isNotBlank()

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = if (produto.tipoItem == ItemType.PRODUTO) "Editar Produto" else "Editar Serviço",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismissRequest) {
                            Icon(Icons.Default.Close, contentDescription = "Fechar")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Imagem
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable {
                                imagePickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedImageUri != null) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Imagem selecionada",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.AddAPhoto,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "Alterar Imagem",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Nome
                    OutlinedTextField(
                        value = nome,
                        onValueChange = { nome = it },
                        label = { Text(if (produto.tipoItem == ItemType.PRODUTO) "Nome do Produto" else "Nome do Serviço") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Categorias
                    Text("Categoria", style = MaterialTheme.typography.labelLarge)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        categorias.filter { it.id != "1" }.forEach { categoria ->
                            val isSelected = categoryId == categoria.id
                            FilterChip(
                                selected = isSelected,
                                onClick = { categoryId = if (isSelected) null else categoria.id },
                                label = { Text(categoria.nome) }
                            )
                        }
                    }

                    if (produto.tipoItem == ItemType.PRODUTO) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = precoVendaText,
                                onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) precoVendaText = it },
                                label = { Text("Preço Venda") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                prefix = { Text("R$ ") }
                            )
                            OutlinedTextField(
                                value = precoCustoText,
                                onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) precoCustoText = it },
                                label = { Text("Preço Custo") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                prefix = { Text("R$ ") }
                            )
                        }

                        Text("Unidade de Medida", style = MaterialTheme.typography.labelLarge)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            UnitType.values().forEach { unit ->
                                val isSelected = unidadeMedida == unit
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { unidadeMedida = unit },
                                    label = { Text(unit.name) }
                                )
                            }
                        }
                    } else {
                        OutlinedTextField(
                            value = precoVendaText,
                            onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) precoVendaText = it },
                            label = { Text("Preço Venda") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            prefix = { Text("R$ ") }
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = {
                            onConfirmar(
                                produto.id,
                                nome,
                                categoryId,
                                precoVendaText.toDoubleOrNull() ?: 0.0,
                                precoCustoText.toDoubleOrNull() ?: 0.0,
                                unidadeMedida,
                                selectedImageUri?.toString(),
                                produto.tipoItem,
                                produto.quantidadeEstoque
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isConfirmEnabled
                    ) {
                        Text("Salvar Alterações")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogNovoProduto(
    categorias: List<Category>,
    onDismissRequest: () -> Unit,
    onConfirmar: (
        nome: String,
        categoryId: String?,
        precoVenda: Double,
        precoCusto: Double,
        unidadeMedida: UnitType,
        imagePath: String?
    ) -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var categoryId by remember { mutableStateOf<String?>(null) }
    var precoVendaText by remember { mutableStateOf("") }
    var precoCustoText by remember { mutableStateOf("") }
    var unidadeMedida by remember { mutableStateOf(UnitType.UN) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageUri = uri }
    )

    val isConfirmEnabled = nome.isNotBlank() && precoVendaText.isNotBlank()

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Cabeçalho
                CenterAlignedTopAppBar(
                    title = { Text("Novo Produto", style = MaterialTheme.typography.titleLarge) },
                    navigationIcon = {
                        IconButton(onClick = onDismissRequest) {
                            Icon(Icons.Default.Close, contentDescription = "Fechar")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Imagem
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable {
                                imagePickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedImageUri != null) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Imagem selecionada",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.AddAPhoto,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "Adicionar Imagem",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Nome
                    OutlinedTextField(
                        value = nome,
                        onValueChange = { nome = it },
                        label = { Text("Nome do Produto") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Categorias (Tags)
                    Text("Categoria", style = MaterialTheme.typography.labelLarge)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        categorias.filter { it.id != "1" }.forEach { categoria ->
                            val isSelected = categoryId == categoria.id
                            FilterChip(
                                selected = isSelected,
                                onClick = { categoryId = if (isSelected) null else categoria.id },
                                label = { Text(categoria.nome) }
                            )
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Preço de Venda
                        OutlinedTextField(
                            value = precoVendaText,
                            onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) precoVendaText = it },
                            label = { Text("Preço Venda") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            prefix = { Text("R$ ") }
                        )
                        // Preço de Custo
                        OutlinedTextField(
                            value = precoCustoText,
                            onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) precoCustoText = it },
                            label = { Text("Preço Custo") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            prefix = { Text("R$ ") }
                        )
                    }

                    // Unidade de Medida
                    Text("Unidade de Medida", style = MaterialTheme.typography.labelLarge)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        UnitType.values().forEach { unit ->
                            val isSelected = unidadeMedida == unit
                            FilterChip(
                                selected = isSelected,
                                onClick = { unidadeMedida = unit },
                                label = { Text(unit.name) }
                            )
                        }
                    }
                }

                // Ação
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = {
                            onConfirmar(
                                nome,
                                categoryId,
                                precoVendaText.toDoubleOrNull() ?: 0.0,
                                precoCustoText.toDoubleOrNull() ?: 0.0,
                                unidadeMedida,
                                selectedImageUri?.toString()
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isConfirmEnabled
                    ) {
                        Text("Confirmar")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogNovoServico(
    categorias: List<Category>,
    onDismissRequest: () -> Unit,
    onConfirmar: (
        nome: String,
        categoryId: String?,
        precoVenda: Double,
        imagePath: String?
    ) -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var categoryId by remember { mutableStateOf<String?>(null) }
    var precoVendaText by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageUri = uri }
    )

    val isConfirmEnabled = nome.isNotBlank() && precoVendaText.isNotBlank()

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Cabeçalho
                CenterAlignedTopAppBar(
                    title = { Text("Novo Serviço", style = MaterialTheme.typography.titleLarge) },
                    navigationIcon = {
                        IconButton(onClick = onDismissRequest) {
                            Icon(Icons.Default.Close, contentDescription = "Fechar")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Imagem
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable {
                                imagePickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedImageUri != null) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Imagem selecionada",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.AddAPhoto,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "Adicionar Imagem",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Nome
                    OutlinedTextField(
                        value = nome,
                        onValueChange = { nome = it },
                        label = { Text("Nome do Serviço") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Categorias (Tags)
                    Text("Categoria", style = MaterialTheme.typography.labelLarge)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        categorias.filter { it.id != "1" }.forEach { categoria ->
                            val isSelected = categoryId == categoria.id
                            FilterChip(
                                selected = isSelected,
                                onClick = { categoryId = if (isSelected) null else categoria.id },
                                label = { Text(categoria.nome) }
                            )
                        }
                    }

                    // Preço de Venda
                    OutlinedTextField(
                        value = precoVendaText,
                        onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) precoVendaText = it },
                        label = { Text("Preço Venda") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        prefix = { Text("R$ ") }
                    )
                }

                // Ação
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = {
                            onConfirmar(
                                nome,
                                categoryId,
                                precoVendaText.toDoubleOrNull() ?: 0.0,
                                selectedImageUri?.toString()
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isConfirmEnabled
                    ) {
                        Text("Confirmar")
                    }
                }
            }
        }
    }
}
