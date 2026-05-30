package com.anotasmart.ui.screens.categorias

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anotasmart.AnotaSmartApplication
import com.anotasmart.model.CategoryType
import com.anotasmart.ui.components.DialogNovaCategoria
import com.anotasmart.ui.components.DoubleDeleteConfirmationDialog
import com.anotasmart.ui.viewModels.CategoriasViewModel
import com.anotasmart.ui.viewModels.CategoriasViewModelFactory
import androidx.compose.foundation.combinedClickable

@OptIn(ExperimentalLayoutApi::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun CategoriasScreen() {
    val context = LocalContext.current
    val database = (context.applicationContext as AnotaSmartApplication).database
    val viewModel: CategoriasViewModel = viewModel(
        factory = CategoriasViewModelFactory(database.categoryDao())
    )

    val selectedType by viewModel.selectedType.collectAsState()
    val categorias by viewModel.categorias.collectAsState()
    val categoriasFiltradas = categorias.filter { it.tipo == selectedType && it.id != "1" }
    val mostrarModalNovaCategoria by viewModel.mostrarModalNovaCategoria.collectAsState()
    val categoriaParaDeletar by viewModel.categoriaParaDeletar.collectAsState()
    val mensagemErro by viewModel.mensagemErro.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(mensagemErro) {
        mensagemErro?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.limparErro()
        }
    }

    DoubleDeleteConfirmationDialog(
        showDialog = categoriaParaDeletar != null,
        onDismissRequest = { viewModel.fecharModalDelecao() },
        onConfirm = { categoriaParaDeletar?.let { viewModel.deletarCategoria(it) } },
        title = "Excluir Categoria",
        message1 = "Deseja excluir a categoria '${categoriaParaDeletar?.nome}'?",
        message2 = "Esta ação removerá a categoria permanentemente. Confirmar?",
        confirmButtonText = "Excluir Categoria"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Seletor de Tipo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TipoItemSelector(
                    title = "Produtos",
                    icon = Icons.Default.Inventory,
                    isSelected = selectedType == CategoryType.ITENS,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.selectType(CategoryType.ITENS) }
                )
                TipoItemSelector(
                    title = "Despesas",
                    icon = Icons.Default.MoneyOff,
                    isSelected = selectedType == CategoryType.DESPESAS,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.selectType(CategoryType.DESPESAS) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Lista de Categorias em FlowRow
            Text(
                text = "Categorias de ${if (selectedType == CategoryType.ITENS) "Produtos" else "Despesas"}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                categoriasFiltradas.forEach { categoria ->
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .combinedClickable(
                                onClick = { },
                                onLongClick = { viewModel.selecionarCategoriaParaDelecao(categoria) }
                            ),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        tonalElevation = 1.dp,
                        shape = RoundedCornerShape(8.dp),
                        border = AssistChipDefaults.assistChipBorder(enabled = true)
                    ) {
                        Text(
                            text = categoria.nome,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        ExtendedFloatingActionButton(
            onClick = { viewModel.abrirModalNovaCategoria() },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            icon = { Icon(Icons.Default.Add, contentDescription = null) },
            text = { Text("Cadastrar categoria") }
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp) // Acima do FAB
        )

        if (mostrarModalNovaCategoria) {
            DialogNovaCategoria(
                tipoInicial = selectedType,
                onDismissRequest = { viewModel.fecharModalNovaCategoria() },
                onConfirmar = { nome, tipo ->
                    viewModel.salvarNovaCategoria(nome, tipo)
                }
            )
        }
    }
}

@Composable
fun TipoItemSelector(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = title, color = contentColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}
