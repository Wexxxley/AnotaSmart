package com.anotasmart.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.anotasmart.database.dao.ProductDao
import com.anotasmart.model.CartItem
import com.anotasmart.model.ItemType
import com.anotasmart.model.UnitType
import com.anotasmart.model.entity.Product
import com.anotasmart.utils.ImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class ProdutosVendasViewModel(
    private val productDao: ProductDao,
    private val cartItems: StateFlow<List<CartItem>> = MutableStateFlow(emptyList())
) : ViewModel() {
    
    val produtos: StateFlow<List<Product>> = productDao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isManagementMode = MutableStateFlow(false)
    val isManagementMode: StateFlow<Boolean> = _isManagementMode.asStateFlow()

    private val _categoriaSelecionada = MutableStateFlow("1")
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

    private val _mensagemErro = MutableStateFlow<String?>(null)
    val mensagemErro: StateFlow<String?> = _mensagemErro.asStateFlow()

    private val _produtoParaDeletar = MutableStateFlow<Product?>(null)
    val produtoParaDeletar: StateFlow<Product?> = _produtoParaDeletar.asStateFlow()

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

    fun toggleManagementMode() {
        _isManagementMode.value = !_isManagementMode.value
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCategoriaSelecionada(id: String) {
        _categoriaSelecionada.value = id
    }

    fun limparErro() {
        _mensagemErro.value = null
    }

    // Gerenciamento
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

    fun selecionarProdutoParaDelecao(produto: Product) {
        val noCarrinho = cartItems.value.any { it.product?.id == produto.id }
        if (noCarrinho) {
            _mensagemErro.value = "Não é possível excluir este item pois ele está no carrinho."
            fecharModalEdicao()
            return
        }
        _produtoParaDeletar.value = produto
    }

    fun fecharModalDelecao() {
        _produtoParaDeletar.value = null
    }

    fun deletarProduto(produto: Product) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                produto.imagePath?.let { path ->
                    ImageUtils.deleteImageFromInternalStorage(path)
                }
                val produtoDeletado = produto.copy(
                    isDeleted = true,
                    imagePath = null
                )
                productDao.update(produtoDeletado)
            }
            fecharModalDelecao()
            fecharModalEdicao()
        }
    }

    fun salvarNovoProduto(nome: String, categoryId: String?, precoVenda: Double, precoCusto: Double, unidadeMedida: UnitType, imagePath: String?) {
        viewModelScope.launch {
            val novoProduto = Product(
                id = UUID.randomUUID().toString(),
                categoryId = categoryId,
                nome = nome,
                precoCusto = precoCusto,
                precoVenda = precoVenda,
                unidadeMedida = unidadeMedida,
                tipoItem = ItemType.PRODUTO,
                quantidadeEstoque = 0.0,
                imagePath = imagePath
            )
            withContext(Dispatchers.IO) { productDao.insert(novoProduto) }
            fecharModalNovoProduto()
        }
    }

    fun salvarNovoServico(nome: String, categoryId: String?, precoVenda: Double, imagePath: String?) {
        viewModelScope.launch {
            val novoServico = Product(
                id = UUID.randomUUID().toString(),
                categoryId = categoryId,
                nome = nome,
                precoCusto = 0.0,
                precoVenda = precoVenda,
                unidadeMedida = UnitType.UN,
                tipoItem = ItemType.SERVICO,
                quantidadeEstoque = 0.0,
                imagePath = imagePath
            )
            withContext(Dispatchers.IO) { productDao.insert(novoServico) }
            fecharModalNovoServico()
        }
    }

    fun salvarEdicao(id: String, nome: String, categoryId: String?, precoVenda: Double, precoCusto: Double, unidadeMedida: UnitType, imagePath: String?, tipoItem: ItemType, quantidadeEstoque: Double) {
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
            withContext(Dispatchers.IO) { productDao.update(produtoEditado) }
            fecharModalEdicao()
        }
    }

    fun confirmarEntradaEstoque(produtoId: String, quantidade: Double, novoPrecoCusto: Double) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val p = productDao.getById(produtoId)
                if (p != null) {
                    val pAtualizado = p.copy(quantidadeEstoque = p.quantidadeEstoque + quantidade, precoCusto = novoPrecoCusto)
                    productDao.update(pAtualizado)
                }
            }
            fecharModalEstoque()
        }
    }

    // Vendas
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

class ProdutosVendasViewModelFactory(
    private val productDao: ProductDao,
    private val cartItems: StateFlow<List<CartItem>> = MutableStateFlow(emptyList())
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProdutosVendasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProdutosVendasViewModel(productDao, cartItems) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
