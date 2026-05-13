package com.anotasmart.ui.viewModels

import androidx.lifecycle.ViewModel
import com.anotasmart.data.mocks.MockDataSource
import com.anotasmart.model.CategoryType
import com.anotasmart.model.entity.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.util.UUID

class CategoriasViewModel : ViewModel() {
    private val _categorias = MutableStateFlow<List<Category>>(emptyList())
    val categorias: StateFlow<List<Category>> = _categorias.asStateFlow()

    private val _selectedType = MutableStateFlow(CategoryType.ITENS)
    val selectedType: StateFlow<CategoryType> = _selectedType.asStateFlow()

    private val _mostrarModalNovaCategoria = MutableStateFlow(false)
    val mostrarModalNovaCategoria: StateFlow<Boolean> = _mostrarModalNovaCategoria.asStateFlow()

    val categoriasItens = _categorias.map { list ->
        val filtered = list.filter { it.tipo == CategoryType.ITENS }
        listOf(Category(id = "1", nome = "TODOS", tipo = CategoryType.ITENS)) + filtered
    }

    val categoriasDespesas = _categorias.map { list ->
        list.filter { it.tipo == CategoryType.DESPESAS }
    }

    init {
        _categorias.value = MockDataSource.getMockCategories()
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

    fun salvarNovaCategoria(nome: String, tipo: CategoryType) {
        val novaCategoria = Category(
            id = UUID.randomUUID().toString(),
            nome = nome,
            tipo = tipo
        )
        val listaAtual = _categorias.value.toMutableList()
        listaAtual.add(novaCategoria)
        _categorias.value = listaAtual
        fecharModalNovaCategoria()
    }

    fun getCategoriasFiltradas(): List<Category> {
        return _categorias.value.filter { it.tipo == _selectedType.value && it.id != "1" }
    }
}
