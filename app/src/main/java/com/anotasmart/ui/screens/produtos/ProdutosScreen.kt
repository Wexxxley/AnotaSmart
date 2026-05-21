package com.anotasmart.ui.screens.produtos

import android.net.Uri
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

import androidx.compose.ui.platform.LocalContext
import com.anotasmart.AnotaSmartApplication
import com.anotasmart.model.CartItem
import com.anotasmart.model.ImageDirectory
import com.anotasmart.ui.components.DoubleDeleteConfirmationDialog
import com.anotasmart.ui.viewModels.CategoriasViewModelFactory
import com.anotasmart.ui.viewModels.ProdutosViewModelFactory
import com.anotasmart.utils.ImageUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ProdutosScreen(
    cartItems: StateFlow<List<CartItem>> = MutableStateFlow(emptyList()),
    produtosViewModel: ProdutosViewModel? = null
) {
    val context = LocalContext.current
    val database = (context.applicationContext as AnotaSmartApplication).database
    
    // Se o viewModel não for passado (ex: via NavHost), cria um localmente (sem cartItems)
    // Mas no fluxo principal, o MainActivity passará o viewModel com cartItems injetado.
    val viewModel: ProdutosViewModel = produtosViewModel ?: viewModel(
        factory = ProdutosViewModelFactory(database.productDao(), cartItems)
    )

    val categoriasViewModel: CategoriasViewModel = viewModel(
        factory = CategoriasViewModelFactory(database.categoryDao())
    )
    val produtos by viewModel.produtosFiltrados.collectAsState(initial = emptyList())
    val categorias by categoriasViewModel.categoriasItens.collectAsState(initial = emptyList())
    val categoriaSelecionada by viewModel.categoriaSelecionada.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val produtoParaEditar by viewModel.produtoParaEditar.collectAsState()
    val produtoParaEstoque by viewModel.produtoParaEstoque.collectAsState()
    val mostrarModalNovoProduto by viewModel.mostrarModalNovoProduto.collectAsState()
    val mostrarModalNovoServico by viewModel.mostrarModalNovoServico.collectAsState()
    val produtoParaDeletar by viewModel.produtoParaDeletar.collectAsState()
    val mensagemErro by viewModel.mensagemErro.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var expandedFab by remember { mutableStateOf(false) }

    LaunchedEffect(mensagemErro) {
        mensagemErro?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.limparErro()
        }
    }

    // Diálogo de Confirmação de Exclusão (Dupla Etapa)
    DoubleDeleteConfirmationDialog(
        showDialog = produtoParaDeletar != null,
        onDismissRequest = { viewModel.fecharModalDelecao() },
        onConfirm = { produtoParaDeletar?.let { viewModel.deletarProduto(it) } },
        title = if ((produtoParaDeletar?.quantidadeEstoque ?: 0.0) > 0) "Aviso de Estoque" else "Confirmar Exclusão",
        message1 = produtoParaDeletar?.let { produto ->
            if (produto.quantidadeEstoque > 0) {
                "ATENÇÃO: Este produto ainda possui ${produto.quantidadeEstoque} unidades em estoque. Ao excluir, você perderá o controle deste saldo. Deseja continuar?"
            } else {
                "Tem certeza que deseja excluir '${produto.nome}'? Ele não aparecerá mais nas suas listas, mas o histórico de vendas passadas será preservado."
            }
        } ?: "",
        message2 = "O item será removido das listas de seleção, mas o histórico financeiro será mantido. Confirmar?",
        confirmButtonText = "Confirmar Exclusão"
    )

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
                    modifier = Modifier.padding(bottom = 8.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
                ExtendedFloatingActionButton(
                    onClick = {
                        viewModel.abrirModalNovoProduto()
                        expandedFab = false
                    },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Produto") },
                    modifier = Modifier.padding(bottom = 8.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            }
            ExtendedFloatingActionButton(
                onClick = { expandedFab = !expandedFab },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Adicionar novo"
                    )
                },
                text = { Text("Cadastrar item") }
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 80.dp)
        )

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
                onConfirmar = { nome, categoryId, precoVenda, precoCusto, unidade, imageUriString ->
                    val internalPath = imageUriString?.let {
                        if (it.startsWith("content://")) {
                            ImageUtils.saveImageToInternalStorage(context, Uri.parse(it), ImageDirectory.PRODUCTS)
                        } else it
                    }
                    viewModel.salvarNovoProduto(nome, categoryId, precoVenda, precoCusto, unidade, internalPath)
                },
                onNovaCategoria = { nome, tipo ->
                    categoriasViewModel.salvarNovaCategoria(nome, tipo)
                }
            )
        }

        if (mostrarModalNovoServico) {
            DialogNovoServico(
                categorias = categorias,
                onDismissRequest = { viewModel.fecharModalNovoServico() },
                onConfirmar = { nome, categoryId, precoVenda, imageUriString ->
                    val internalPath = imageUriString?.let {
                        if (it.startsWith("content://")) {
                            ImageUtils.saveImageToInternalStorage(context, Uri.parse(it), ImageDirectory.SERVICES)
                        } else it
                    }
                    viewModel.salvarNovoServico(nome, categoryId, precoVenda, internalPath)
                },
                onNovaCategoria = { nome, tipo ->
                    categoriasViewModel.salvarNovaCategoria(nome, tipo)
                }
            )
        }

        produtoParaEditar?.let { produto ->
            DialogEditarItem(
                produto = produto,
                categorias = categorias,
                onDismissRequest = { viewModel.fecharModalEdicao() },
                onConfirmar = { id, nome, categoryId, precoVenda, precoCusto, unidade, imageUriString, tipoItem, estoque ->
                    val internalPath = imageUriString?.let {
                        if (it.startsWith("content://")) {
                            val directory = if (tipoItem == com.anotasmart.model.ItemType.PRODUTO) ImageDirectory.PRODUCTS else ImageDirectory.SERVICES
                            ImageUtils.saveImageToInternalStorage(context, Uri.parse(it), directory)
                        } else it
                    }
                    viewModel.salvarEdicao(id, nome, categoryId, precoVenda, precoCusto, unidade, internalPath, tipoItem, estoque)
                },
                onDeletar = { viewModel.selecionarProdutoParaDelecao(it) },
                onNovaCategoria = { nome, tipo ->
                    categoriasViewModel.salvarNovaCategoria(nome, tipo)
                }
            )
        }
    }
}
