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
import com.anotasmart.ui.components.ListaCategoriasProdutos
import com.anotasmart.ui.screens.produtos.components.DialogEditarItem
import com.anotasmart.ui.screens.produtos.components.DialogEntradaEstoque
import com.anotasmart.ui.screens.produtos.components.DialogNovoProduto
import com.anotasmart.ui.screens.produtos.components.DialogNovoServico
import com.anotasmart.ui.screens.produtos.components.GradeItemsProduto
import com.anotasmart.ui.viewModels.CategoriasViewModel
import com.anotasmart.ui.viewModels.ProdutosViewModel

@Composable
fun ProdutosScreen(
    viewModel: ProdutosViewModel = viewModel(),
    categoriasViewModel: CategoriasViewModel = viewModel()
) {
    val produtos by viewModel.produtosFiltrados.collectAsState(initial = emptyList())
    val categorias by categoriasViewModel.categoriasItens.collectAsState(initial = emptyList())
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

            ListaCategoriasProdutos(
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

        // FAB e Menu de Opções posicionados manualmente para evitar Scaffold aninhado
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
            DialogNovoProduto(
                categorias = categorias,
                onDismissRequest = { viewModel.fecharModalNovoProduto() },
                onConfirmar = { nome, categoryId, precoVenda, precoCusto, unidade, imagePath ->
                    viewModel.salvarNovoProduto(nome, categoryId, precoVenda, precoCusto, unidade, imagePath)
                }
            )
        }

        if (mostrarModalNovoServico) {
            DialogNovoServico(
                categorias = categorias,
                onDismissRequest = { viewModel.fecharModalNovoServico() },
                onConfirmar = { nome, categoryId, precoVenda, imagePath ->
                    viewModel.salvarNovoServico(nome, categoryId, precoVenda, imagePath)
                }
            )
        }

        produtoParaEditar?.let { produto ->
            DialogEditarItem(
                produto = produto,
                categorias = categorias,
                onDismissRequest = { viewModel.fecharModalEdicao() },
                onConfirmar = { id, nome, categoryId, precoVenda, precoCusto, unidade, imagePath, tipoItem, estoque ->
                    viewModel.salvarEdicao(id, nome, categoryId, precoVenda, precoCusto, unidade, imagePath, tipoItem, estoque)
                }
            )
        }
    }
}
