package com.anotasmart.ui.screens.categorias

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.anotasmart.database.dao.CategoryDao
import com.anotasmart.model.CategoryType
import com.anotasmart.model.entity.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class CategoriasViewModel(private val categoryDao: CategoryDao) : ViewModel() {
    val categorias: StateFlow<List<Category>> = categoryDao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedType = MutableStateFlow(CategoryType.ITENS)
    val selectedType: StateFlow<CategoryType> = _selectedType.asStateFlow()

    private val _mostrarModalNovaCategoria = MutableStateFlow(false)
    val mostrarModalNovaCategoria: StateFlow<Boolean> = _mostrarModalNovaCategoria.asStateFlow()

    val categoriasItens = categorias.map { list ->
        val filtered = list.filter { it.tipo == CategoryType.ITENS }
        listOf(Category(id = "1", nome = "TODOS", tipo = CategoryType.ITENS)) + filtered
    }

    val categoriasDespesas = categorias.map { list ->
        list.filter { it.tipo == CategoryType.DESPESAS }
    }

    fun selectType(type: CategoryType) {
        _selectedType.value = type
    }

    fun abrirModalNovaCategoria() {
        _mostrarModalNovaCategoria.value = true
    }

    fun fecharModalNovaCategoria() {
        _mostrarModalNovaCategoria.value = false
    }

    private val _categoriaParaDeletar = MutableStateFlow<Category?>(null)
    val categoriaParaDeletar: StateFlow<Category?> = _categoriaParaDeletar.asStateFlow()

    private val _mensagemErro = MutableStateFlow<String?>(null)
    val mensagemErro: StateFlow<String?> = _mensagemErro.asStateFlow()

    fun limparErro() {
        _mensagemErro.value = null
    }

    fun selecionarCategoriaParaDelecao(categoria: Category) {
        viewModelScope.launch {
            val countProducts = withContext(Dispatchers.IO) {
                categoryDao.countProductsByCategory(categoria.id)
            }
            val countExpenses = withContext(Dispatchers.IO) {
                categoryDao.countExpensesByCategory(categoria.id)
            }

            if (countProducts > 0 || countExpenses > 0) {
                _mensagemErro.value = "Não é possível excluir esta categoria pois ela possui itens ou despesas associados."
                return@launch
            }

            _categoriaParaDeletar.value = categoria
        }
    }

    fun fecharModalDelecao() {
        _categoriaParaDeletar.value = null
    }

    fun deletarCategoria(categoria: Category) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                categoryDao.delete(categoria)
            }
            fecharModalDelecao()
        }
    }

    fun salvarNovaCategoria(nome: String, tipo: CategoryType) {
        viewModelScope.launch {
            val novaCategoria = Category(
                id = UUID.randomUUID().toString(),
                nome = nome,
                tipo = tipo
            )
            withContext(Dispatchers.IO) {
                categoryDao.insert(novaCategoria)
            }
            fecharModalNovaCategoria()
        }
    }

    fun getCategoriasFiltradas(): List<Category> {
        return categorias.value.filter { it.tipo == _selectedType.value && it.id != "1" }
    }
}

class CategoriasViewModelFactory(private val categoryDao: CategoryDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoriasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoriasViewModel(categoryDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
