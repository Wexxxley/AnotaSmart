package com.anotasmart.ui.viewModels

import androidx.lifecycle.ViewModel
import com.anotasmart.data.mocks.MockDataSource
import com.anotasmart.model.CartItem
import com.anotasmart.model.entity.Category
import com.anotasmart.model.entity.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class VendaViewModel : ViewModel() {
    private val _produtos = MutableStateFlow<List<Product>>(emptyList())
    val produtos: StateFlow<List<Product>> = _produtos.asStateFlow()
    private val _categorias = MutableStateFlow<List<Category>>(emptyList())
    val categorias: StateFlow<List<Category>> = _categorias.asStateFlow()
    private val _categoriaSelecionada = MutableStateFlow("1")
    val categoriaSelecionada: StateFlow<String> = _categoriaSelecionada.asStateFlow()
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    private val _produtoSelecionado = MutableStateFlow<Product?>(null)
    val produtoSelecionado: StateFlow<Product?> = _produtoSelecionado.asStateFlow()
    private val _mostrarDialogItemAvulso = MutableStateFlow(false)
    val mostrarDialogItemAvulso: StateFlow<Boolean> = _mostrarDialogItemAvulso.asStateFlow()

    init {
        carregarDadosMock()
    }

    private fun carregarDadosMock() {
        _categorias.value = MockDataSource.getMockCategories()
        _produtos.value = MockDataSource.getMockProducts()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCategoriaSelecionada(id: String) {
        _categoriaSelecionada.value = id
    }

    fun selecionarProdutoParaCarrinho(produto: Product) {
        _produtoSelecionado.value = produto
    }

    fun limparProdutoSelecionado() {
        _produtoSelecionado.value = null
    }

    fun abrirDialogItemAvulso() {
        _mostrarDialogItemAvulso.value = true
    }

    fun fecharDialogItemAvulso() {
        _mostrarDialogItemAvulso.value = false
    }

    fun adicionarAoCarrinho(produto: Product, quantidade: Double, onConfirmar: (CartItem) -> Unit) {
        val item = CartItem(
            product = produto,
            nome = produto.nome,
            precoVenda = produto.precoVenda,
            precoCusto = produto.precoCusto,
            quantidade = quantidade,
            unidadeMedida = produto.unidadeMedida
        )
        onConfirmar(item)
        limparProdutoSelecionado()
    }

    fun adicionarItemAvulsoAoCarrinho(precoCusto: Double?, precoVenda: Double, quantidade: Double, onConfirmar: (CartItem) -> Unit) {
        val item = CartItem(
            nome = "Item Avulso",
            precoVenda = precoVenda,
            precoCusto = precoCusto,
            quantidade = quantidade
        )
        onConfirmar(item)
        fecharDialogItemAvulso()
    }
}
