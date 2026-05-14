package com.anotasmart.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.anotasmart.database.dao.ProductDao
import com.anotasmart.model.CartItem
import com.anotasmart.model.entity.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class VendaViewModel(private val productDao: ProductDao) : ViewModel() {
    val produtos: StateFlow<List<Product>> = productDao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _categoriaSelecionada = MutableStateFlow("1")
    val categoriaSelecionada: StateFlow<String> = _categoriaSelecionada.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _produtoSelecionado = MutableStateFlow<Product?>(null)
    val produtoSelecionado: StateFlow<Product?> = _produtoSelecionado.asStateFlow()

    private val _mostrarDialogItemAvulso = MutableStateFlow(false)
    val mostrarDialogItemAvulso: StateFlow<Boolean> = _mostrarDialogItemAvulso.asStateFlow()

    val produtosFiltrados = combine(produtos, _categoriaSelecionada, _searchQuery) { produtos, categoriaId, query ->
        produtos.filter { produto ->
            val matchesCategory = if (categoriaId == "1") true else produto.categoryId == categoriaId
            val matchesSearch = produto.nome.contains(query, ignoreCase = true)
            matchesCategory && matchesSearch
        }
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

class VendaViewModelFactory(private val productDao: ProductDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VendaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VendaViewModel(productDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
