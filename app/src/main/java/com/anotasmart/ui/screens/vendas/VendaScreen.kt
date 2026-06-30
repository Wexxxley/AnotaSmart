package com.anotasmart.ui.screens.vendas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anotasmart.ui.components.BarraBusca
import com.anotasmart.ui.components.ListaCategoriasProdutos
import com.anotasmart.ui.screens.vendas.components.DialogAdicionarCarrinho
import com.anotasmart.ui.screens.vendas.components.DialogItemAvulso
import com.anotasmart.ui.screens.vendas.components.GradeItems
import com.anotasmart.ui.viewModels.CartViewModel
import com.anotasmart.ui.viewModels.CategoriasViewModel
import com.anotasmart.ui.viewModels.VendaViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anotasmart.AnotaSmartApplication
import com.anotasmart.ui.viewModels.CategoriasViewModelFactory
import com.anotasmart.ui.viewModels.VendaViewModelFactory

@Composable
fun VendaScreen(
    cartViewModel: CartViewModel
) {
    val context = LocalContext.current
    val database = (context.applicationContext as AnotaSmartApplication).database
    val viewModel: VendaViewModel = viewModel(
        factory = VendaViewModelFactory(database.productDao())
    )
    val categoriasViewModel: CategoriasViewModel = viewModel(
        factory = CategoriasViewModelFactory(database.categoryDao())
    )
    val produtos by viewModel.produtosFiltrados.collectAsState(initial = emptyList())
    val categorias by categoriasViewModel.categoriasItens.collectAsState(initial = emptyList())
    val categoriaSelecionada by viewModel.categoriaSelecionada.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val produtoSelecionado by viewModel.produtoSelecionado.collectAsState()
    val mostrarDialogItemAvulso by viewModel.mostrarDialogItemAvulso.collectAsState()
    val itensNoCarrinho by cartViewModel.items.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        BarraBusca(
            query = searchQuery,
            onQueryChange = viewModel::onSearchQueryChanged
        )

        // venda avulsa
        Button(
            onClick = { viewModel.abrirDialogItemAvulso() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Item avulso",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(text = "ITEM AVULSO", fontWeight = FontWeight.Bold)
        }

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
        val quantidadeNoCarrinho = itensNoCarrinho.find { it.product?.id == produto.id }?.quantidade ?: 0.0
        DialogAdicionarCarrinho(
            produto = produto,
            quantidadeNoCarrinho = quantidadeNoCarrinho,
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
