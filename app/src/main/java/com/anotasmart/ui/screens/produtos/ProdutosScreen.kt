package com.anotasmart.ui.screens.produtos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anotasmart.ui.components.BarraBusca
import com.anotasmart.ui.components.ListaCategorias
import com.anotasmart.ui.screens.produtos.components.DialogEntradaEstoque
import com.anotasmart.ui.screens.produtos.components.GradeItemsProduto
import com.anotasmart.ui.viewModels.ProdutosViewModel

@Composable
fun ProdutosScreen(
    viewModel: ProdutosViewModel = viewModel()
) {
    val produtos by viewModel.produtosFiltrados.collectAsState(initial = emptyList())
    val categorias by viewModel.categorias.collectAsState()
    val categoriaSelecionada by viewModel.categoriaSelecionada.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val produtoParaEditar by viewModel.produtoParaEditar.collectAsState()
    val produtoParaEstoque by viewModel.produtoParaEstoque.collectAsState()
    val mostrarModalNovoProduto by viewModel.mostrarModalNovoProduto.collectAsState()
    val mostrarModalNovoServico by viewModel.mostrarModalNovoServico.collectAsState()
    var expandedFab by remember { mutableStateOf(false) }

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

            ListaCategorias(
                categorias = categorias,
                categoriaSelecionadaId = categoriaSelecionada,
                onCategoriaClick = viewModel::onCategoriaSelecionada
            )

            GradeItemsProduto(
                produtos = produtos,
                onProdutoClick = { viewModel.selecionarProdutoParaEdicao(it) },
                onAddEstoqueClick = { viewModel.selecionarProdutoParaEstoque(it) }
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            horizontalAlignment = Alignment.End
        ) {
            if (expandedFab) {
                ExtendedFloatingActionButton(
                    onClick = {
                        viewModel.abrirModalNovoServico()
                        expandedFab = false
                    },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Serviço") },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                ExtendedFloatingActionButton(
                    onClick = {
                        viewModel.abrirModalNovoProduto()
                        expandedFab = false
                    },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Produto") },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            FloatingActionButton(
                onClick = { expandedFab = !expandedFab },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar novo"
                )
            }
        }

        // Modais
        produtoParaEstoque?.let { produto ->
            DialogEntradaEstoque(
                produto = produto,
                onDismissRequest = { viewModel.fecharModalEstoque() },
                onConfirmar = { quantidade, novoPrecoCusto ->
                    viewModel.confirmarEntradaEstoque(produto.id, quantidade, novoPrecoCusto)
                }
            )
        }

        if (mostrarModalNovoProduto) {
            AlertDialog(
                onDismissRequest = { viewModel.fecharModalNovoProduto() },
                title = { Text("Novo Produto") },
                text = { Text("Aqui virá o formulário de novo produto.") },
                confirmButton = {
                    Button(onClick = { viewModel.fecharModalNovoProduto() }) {
                        Text("Salvar")
                    }
                }
            )
        }

        if (mostrarModalNovoServico) {
            AlertDialog(
                onDismissRequest = { viewModel.fecharModalNovoServico() },
                title = { Text("Novo Serviço") },
                text = { Text("Aqui virá o formulário de novo serviço.") },
                confirmButton = {
                    Button(onClick = { viewModel.fecharModalNovoServico() }) {
                        Text("Salvar")
                    }
                }
            )
        }

        produtoParaEditar?.let { produto ->
            AlertDialog(
                onDismissRequest = { viewModel.fecharModalEdicao() },
                title = { Text("Editar: ${produto.nome}") },
                text = { Text("Aqui virá o formulário de edição.") },
                confirmButton = {
                    Button(onClick = { viewModel.fecharModalEdicao() }) {
                        Text("Salvar")
                    }
                }
            )
        }
    }
}
