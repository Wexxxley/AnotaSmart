package com.anotasmart.ui.screens.vendas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anotasmart.ui.components.BarraBusca
import com.anotasmart.ui.components.ListaCategoriasProdutos
import com.anotasmart.ui.screens.vendas.components.BotaoVendaAvulsa
import com.anotasmart.ui.screens.vendas.components.DialogAdicionarCarrinho
import com.anotasmart.ui.screens.vendas.components.DialogItemAvulso
import com.anotasmart.ui.screens.vendas.components.GradeItems
import com.anotasmart.ui.viewModels.CartViewModel
import com.anotasmart.ui.viewModels.CategoriasViewModel
import com.anotasmart.ui.viewModels.VendaViewModel

import androidx.compose.ui.platform.LocalContext
import com.anotasmart.AnotaSmartApplication
import com.anotasmart.ui.viewModels.CategoriasViewModelFactory

@Composable
fun VendaScreen(
    viewModel: VendaViewModel = viewModel(),
    cartViewModel: CartViewModel
) {
    val context = LocalContext.current
    val database = (context.applicationContext as AnotaSmartApplication).database
    val categoriasViewModel: CategoriasViewModel = viewModel(
        factory = CategoriasViewModelFactory(database.categoryDao())
    )
    val produtos by viewModel.produtos.collectAsState()
    val categorias by categoriasViewModel.categoriasItens.collectAsState(initial = emptyList())
    val categoriaSelecionada by viewModel.categoriaSelecionada.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val produtoSelecionado by viewModel.produtoSelecionado.collectAsState()
    val mostrarDialogItemAvulso by viewModel.mostrarDialogItemAvulso.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            BarraBusca(
                query = searchQuery,
                onQueryChange = viewModel::onSearchQueryChanged
            )

            BotaoVendaAvulsa(
                onClick = { viewModel.abrirDialogItemAvulso() }
            )

            ListaCategoriasProdutos(
                categorias = categorias,
                categoriaSelecionadaId = categoriaSelecionada,
                onCategoriaClick = viewModel::onCategoriaSelecionada
            )

            GradeItems(
                produtos = produtos,
                onProdutoClick = { produtoClicado ->
                    viewModel.selecionarProdutoParaCarrinho(produtoClicado)
                }
            )
        }

        // Renderização condicional
        produtoSelecionado?.let { produto ->
            DialogAdicionarCarrinho(
                produto = produto,
                onDismissRequest = {
                    viewModel.limparProdutoSelecionado()
                },
                onConfirmar = { quantidade ->
                    viewModel.adicionarAoCarrinho(produto, quantidade) { item ->
                        cartViewModel.adicionarItem(item)
                    }
                }
            )
        }

        // Renderização condicional
        if (mostrarDialogItemAvulso) {
            DialogItemAvulso(
                onDismissRequest = { viewModel.fecharDialogItemAvulso() },
                onConfirmar = { precoCusto, precoVenda, quantidade ->
                    viewModel.adicionarItemAvulsoAoCarrinho(precoCusto, precoVenda, quantidade) { item ->
                        cartViewModel.adicionarItem(item)
                    }
                }
            )
        }
    }
}
