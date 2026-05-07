package com.anotasmart.ui.screens.vendas

import com.anotasmart.ui.screens.vendas.components.DialogAdicionarCarrinho
import com.anotasmart.ui.screens.vendas.components.DialogItemAvulso
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
import com.anotasmart.ui.screens.vendas.components.BotaoVendaAvulsa
import com.anotasmart.ui.screens.vendas.components.GradeProdutos
import com.anotasmart.ui.screens.vendas.components.ListaCategorias
import com.anotasmart.ui.viewModels.VendaViewModel
import com.anotasmart.ui.viewModels.CartViewModel

@Composable
fun VendaScreen(
    viewModel: VendaViewModel = viewModel(),
    cartViewModel: CartViewModel
) {
    val produtos by viewModel.produtos.collectAsState()
    val categorias by viewModel.categorias.collectAsState()
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

            ListaCategorias(
                categorias = categorias,
                categoriaSelecionadaId = categoriaSelecionada,
                onCategoriaClick = viewModel::onCategoriaSelecionada
            )

            GradeProdutos(
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