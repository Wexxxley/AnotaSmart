package com.anotasmart.ui.viewModels

import androidx.lifecycle.ViewModel
import com.anotasmart.ui.screens.MockDataSource
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
}