package com.anotasmart.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.anotasmart.database.dao.ProductDao
import com.anotasmart.model.ItemType
import com.anotasmart.model.entity.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProdutosViewModel(private val productDao: ProductDao) : ViewModel() {
    val produtos: StateFlow<List<Product>> = productDao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _categoriaSelecionada = MutableStateFlow("1") // "1" é "TODOS"
    val categoriaSelecionada: StateFlow<String> = _categoriaSelecionada.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _produtoParaEditar = MutableStateFlow<Product?>(null)
    val produtoParaEditar: StateFlow<Product?> = _produtoParaEditar.asStateFlow()

    private val _produtoParaEstoque = MutableStateFlow<Product?>(null)
    val produtoParaEstoque: StateFlow<Product?> = _produtoParaEstoque.asStateFlow()

    private val _mostrarModalNovoProduto = MutableStateFlow(false)
    val mostrarModalNovoProduto: StateFlow<Boolean> = _mostrarModalNovoProduto.asStateFlow()

    private val _mostrarModalNovoServico = MutableStateFlow(false)
    val mostrarModalNovoServico: StateFlow<Boolean> = _mostrarModalNovoServico.asStateFlow()

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

    fun selecionarProdutoParaEdicao(produto: Product) {
        _produtoParaEditar.value = produto
    }

    fun fecharModalEdicao() {
        _produtoParaEditar.value = null
    }

    fun selecionarProdutoParaEstoque(produto: Product) {
        _produtoParaEstoque.value = produto
    }

    fun fecharModalEstoque() {
        _produtoParaEstoque.value = null
    }

    fun abrirModalNovoProduto() {
        _mostrarModalNovoProduto.value = true
    }

    fun fecharModalNovoProduto() {
        _mostrarModalNovoProduto.value = false
    }

    fun abrirModalNovoServico() {
        _mostrarModalNovoServico.value = true
    }

    fun fecharModalNovoServico() {
        _mostrarModalNovoServico.value = false
    }

    fun salvarNovoProduto(
        nome: String,
        categoryId: String?,
        precoVenda: Double,
        precoCusto: Double,
        unidadeMedida: com.anotasmart.model.UnitType,
        imagePath: String?
    ) {
        viewModelScope.launch {
            val novoProduto = Product(
                id = java.util.UUID.randomUUID().toString(),
                categoryId = categoryId,
                nome = nome,
                precoCusto = precoCusto,
                precoVenda = precoVenda,
                unidadeMedida = unidadeMedida,
                tipoItem = ItemType.PRODUTO,
                quantidadeEstoque = 0.0,
                imagePath = imagePath
            )
            withContext(Dispatchers.IO) {
                productDao.insert(novoProduto)
            }
            fecharModalNovoProduto()
        }
    }

    fun salvarNovoServico(
        nome: String,
        categoryId: String?,
        precoVenda: Double,
        imagePath: String?
    ) {
        viewModelScope.launch {
            val novoServico = Product(
                id = java.util.UUID.randomUUID().toString(),
                categoryId = categoryId,
                nome = nome,
                precoCusto = 0.0,
                precoVenda = precoVenda,
                unidadeMedida = com.anotasmart.model.UnitType.UN,
                tipoItem = ItemType.SERVICO,
                quantidadeEstoque = 0.0,
                imagePath = imagePath
            )
            withContext(Dispatchers.IO) {
                productDao.insert(novoServico)
            }
            fecharModalNovoServico()
        }
    }

    fun salvarEdicao(
        id: String,
        nome: String,
        categoryId: String?,
        precoVenda: Double,
        precoCusto: Double,
        unidadeMedida: com.anotasmart.model.UnitType,
        imagePath: String?,
        tipoItem: ItemType,
        quantidadeEstoque: Double
    ) {
        viewModelScope.launch {
            val produtoEditado = Product(
                id = id,
                categoryId = categoryId,
                nome = nome,
                precoCusto = precoCusto,
                precoVenda = precoVenda,
                unidadeMedida = unidadeMedida,
                tipoItem = tipoItem,
                quantidadeEstoque = quantidadeEstoque,
                imagePath = imagePath
            )
            withContext(Dispatchers.IO) {
                productDao.update(produtoEditado)
            }
            fecharModalEdicao()
        }
    }

    fun confirmarEntradaEstoque(produtoId: String, quantidade: Double, novoPrecoCusto: Double) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val p = productDao.getById(produtoId)
                if (p != null) {
                    val pAtualizado = p.copy(
                        quantidadeEstoque = p.quantidadeEstoque + quantidade,
                        precoCusto = novoPrecoCusto
                    )
                    productDao.update(pAtualizado)
                }
            }
            fecharModalEstoque()
        }
    }
}

class ProdutosViewModelFactory(private val productDao: ProductDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProdutosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProdutosViewModel(productDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
