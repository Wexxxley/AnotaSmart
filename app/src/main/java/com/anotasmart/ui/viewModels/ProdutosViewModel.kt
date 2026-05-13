package com.anotasmart.ui.viewModels

import androidx.lifecycle.ViewModel
import com.anotasmart.data.mocks.MockDataSource
import com.anotasmart.model.ItemType
import com.anotasmart.model.entity.Category
import com.anotasmart.model.entity.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine

class ProdutosViewModel : ViewModel() {
    private val _produtos = MutableStateFlow<List<Product>>(emptyList())

    private val _categoriaSelecionada = MutableStateFlow("1") // "1" é "TODOS" no mock
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

    val produtosFiltrados = combine(_produtos, _categoriaSelecionada, _searchQuery) { produtos, categoriaId, query ->
        produtos.filter { produto ->
            val matchesCategory = if (categoriaId == "1") true else produto.categoryId == categoriaId
            val matchesSearch = produto.nome.contains(query, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    init {
        carregarDadosMock()
    }

    private fun carregarDadosMock() {
        val mockProducts = MockDataSource.getMockProducts().toMutableList()
        _produtos.value = mockProducts
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
        val listaAtual = _produtos.value.toMutableList()
        listaAtual.add(0, novoProduto)
        _produtos.value = listaAtual
        fecharModalNovoProduto()
    }

    fun salvarNovoServico(
        nome: String,
        categoryId: String?,
        precoVenda: Double,
        imagePath: String?
    ) {
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
        val listaAtual = _produtos.value.toMutableList()
        listaAtual.add(0, novoServico)
        _produtos.value = listaAtual
        fecharModalNovoServico()
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
        val produtosAtuais = _produtos.value.toMutableList()
        val index = produtosAtuais.indexOfFirst { it.id == id }
        if (index != -1) {
            produtosAtuais[index] = Product(
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
            _produtos.value = produtosAtuais
        }
        fecharModalEdicao()
    }

    fun confirmarEntradaEstoque(produtoId: String, quantidade: Double, novoPrecoCusto: Double) {
        // Logica para atualizar estoque (no momento apenas simulada)
        val produtosAtuais = _produtos.value.toMutableList()
        val index = produtosAtuais.indexOfFirst { it.id == produtoId }
        if (index != -1) {
            val p = produtosAtuais[index]
            produtosAtuais[index] = p.copy(
                quantidadeEstoque = p.quantidadeEstoque + quantidade,
                precoCusto = novoPrecoCusto
            )
            _produtos.value = produtosAtuais
        }
        fecharModalEstoque()
    }
}
