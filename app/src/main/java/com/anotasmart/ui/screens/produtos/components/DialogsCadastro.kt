package com.anotasmart.ui.screens.produtos.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.anotasmart.model.CategoryType
import com.anotasmart.model.ItemType
import com.anotasmart.model.UnitType
import com.anotasmart.model.entity.Category
import com.anotasmart.model.entity.Product
import com.anotasmart.ui.components.CampoMoeda
import com.anotasmart.ui.components.DialogNovaCategoria
import com.anotasmart.ui.components.FullScreenDialog
import com.anotasmart.ui.components.GradeCategorias

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
    ) -> Unit,
    onDeletar: (Product) -> Unit,
    onNovaCategoria: (String, CategoryType) -> Unit
) {
    var nome by remember { mutableStateOf(produto.nome) }
    var categoryId by remember { mutableStateOf<String?>(produto.categoryId) }
    var precoVendaText by remember { mutableStateOf(produto.precoVenda.toString()) }
    var precoCustoText by remember { mutableStateOf(produto.precoCusto.toString()) }
    var unidadeMedida by remember { mutableStateOf(produto.unidadeMedida) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(produto.imagePath?.let { Uri.parse(it) }) }
    var showNovaCategoriaDialog by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> if (uri != null) selectedImageUri = uri }
    )

    val isConfirmEnabled = nome.isNotBlank() && precoVendaText.isNotBlank()

    if (showNovaCategoriaDialog) {
        DialogNovaCategoria(
            tipoInicial = CategoryType.ITENS,
            onDismissRequest = { showNovaCategoriaDialog = false },
            onConfirmar = { nomeCat, tipo ->
                onNovaCategoria(nomeCat, tipo)
                showNovaCategoriaDialog = false
            }
        )
    }

    FullScreenDialog(
        title = if (produto.tipoItem == ItemType.PRODUTO) "Editar Produto" else "Editar Serviço",
        onDismissRequest = onDismissRequest,
        confirmButtonText = "Salvar Alterações",
        isConfirmEnabled = isConfirmEnabled,
        onConfirmClick = {
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
        }
    ) {
        // Imagem
        ImageSelector(
            uri = selectedImageUri,
            label = if (selectedImageUri != null) "Alterar Imagem" else "Adicionar Imagem",
            onClick = {
                imagePickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        )

        // Nome
        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text(if (produto.tipoItem == ItemType.PRODUTO) "Nome do Produto" else "Nome do Serviço") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Categorias
        Text("Categoria", style = MaterialTheme.typography.labelLarge)
        GradeCategorias(
            categorias = categorias,
            selectedCategoryId = categoryId,
            onCategorySelected = { categoryId = it },
            onAddCategoryClick = { showNovaCategoriaDialog = true }
        )

        if (produto.tipoItem == ItemType.PRODUTO) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CampoMoeda(
                    value = precoVendaText,
                    onValueChange = { precoVendaText = it },
                    label = "Preço Venda",
                    modifier = Modifier.weight(1f)
                )
                CampoMoeda(
                    value = precoCustoText,
                    onValueChange = { precoCustoText = it },
                    label = "Preço Custo",
                    modifier = Modifier.weight(1f)
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
            CampoMoeda(
                value = precoVendaText,
                onValueChange = { precoVendaText = it },
                label = "Preço Venda",
                modifier = Modifier.fillMaxWidth()
            )
        }

        OutlinedButton(
            onClick = { onDeletar(produto) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error)
        ) {
            Text("Excluir ${if (produto.tipoItem == ItemType.PRODUTO) "Produto" else "Serviço"}")
        }
    }
}

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
    ) -> Unit,
    onNovaCategoria: (String, CategoryType) -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var categoryId by remember { mutableStateOf<String?>(null) }
    var precoVendaText by remember { mutableStateOf("") }
    var precoCustoText by remember { mutableStateOf("") }
    var unidadeMedida by remember { mutableStateOf(UnitType.UN) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showNovaCategoriaDialog by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageUri = uri }
    )

    val isConfirmEnabled = nome.isNotBlank() && precoVendaText.isNotBlank()

    if (showNovaCategoriaDialog) {
        DialogNovaCategoria(
            tipoInicial = CategoryType.ITENS,
            onDismissRequest = { showNovaCategoriaDialog = false },
            onConfirmar = { nomeCat, tipo ->
                onNovaCategoria(nomeCat, tipo)
                showNovaCategoriaDialog = false
            }
        )
    }

    FullScreenDialog(
        title = "Novo Produto",
        onDismissRequest = onDismissRequest,
        confirmButtonText = "Confirmar",
        isConfirmEnabled = isConfirmEnabled,
        onConfirmClick = {
            onConfirmar(
                nome,
                categoryId,
                precoVendaText.toDoubleOrNull() ?: 0.0,
                precoCustoText.toDoubleOrNull() ?: 0.0,
                unidadeMedida,
                selectedImageUri?.toString()
            )
        }
    ) {
        ImageSelector(
            uri = selectedImageUri,
            label = "Adicionar Imagem",
            onClick = {
                imagePickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        )

        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome do Produto") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Text("Categoria", style = MaterialTheme.typography.labelLarge)
        GradeCategorias(
            categorias = categorias,
            selectedCategoryId = categoryId,
            onCategorySelected = { categoryId = it },
            onAddCategoryClick = { showNovaCategoriaDialog = true }
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CampoMoeda(
                value = precoVendaText,
                onValueChange = { precoVendaText = it },
                label = "Preço Venda",
                modifier = Modifier.weight(1f)
            )
            CampoMoeda(
                value = precoCustoText,
                onValueChange = { precoCustoText = it },
                label = "Preço Custo",
                modifier = Modifier.weight(1f)
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
    }
}

@Composable
fun DialogNovoServico(
    categorias: List<Category>,
    onDismissRequest: () -> Unit,
    onConfirmar: (
        nome: String,
        categoryId: String?,
        precoVenda: Double,
        imagePath: String?
    ) -> Unit,
    onNovaCategoria: (String, CategoryType) -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var categoryId by remember { mutableStateOf<String?>(null) }
    var precoVendaText by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showNovaCategoriaDialog by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageUri = uri }
    )

    val isConfirmEnabled = nome.isNotBlank() && precoVendaText.isNotBlank()

    if (showNovaCategoriaDialog) {
        DialogNovaCategoria(
            tipoInicial = CategoryType.ITENS,
            onDismissRequest = { showNovaCategoriaDialog = false },
            onConfirmar = { nomeCat, tipo ->
                onNovaCategoria(nomeCat, tipo)
                showNovaCategoriaDialog = false
            }
        )
    }

    FullScreenDialog(
        title = "Novo Serviço",
        onDismissRequest = onDismissRequest,
        confirmButtonText = "Confirmar",
        isConfirmEnabled = isConfirmEnabled,
        onConfirmClick = {
            onConfirmar(
                nome,
                categoryId,
                precoVendaText.toDoubleOrNull() ?: 0.0,
                selectedImageUri?.toString()
            )
        }
    ) {
        ImageSelector(
            uri = selectedImageUri,
            label = "Adicionar Imagem",
            onClick = {
                imagePickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        )

        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome do Serviço") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Text("Categoria", style = MaterialTheme.typography.labelLarge)
        GradeCategorias(
            categorias = categorias,
            selectedCategoryId = categoryId,
            onCategorySelected = { categoryId = it },
            onAddCategoryClick = { showNovaCategoriaDialog = true }
        )

        CampoMoeda(
            value = precoVendaText,
            onValueChange = { precoVendaText = it },
            label = "Preço Venda",
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ImageSelector(
    uri: Uri?,
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (uri != null) {
            AsyncImage(
                model = uri,
                contentDescription = null,
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
                    label,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
