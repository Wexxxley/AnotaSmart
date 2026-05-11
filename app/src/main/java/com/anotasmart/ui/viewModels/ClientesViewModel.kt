package com.anotasmart.ui.viewModels

import androidx.lifecycle.ViewModel
import com.anotasmart.data.mocks.MockDataSource
import com.anotasmart.model.entity.Client
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine

class ClientesViewModel : ViewModel() {
    private val _clientes = MutableStateFlow<List<Client>>(emptyList())
    val clientes: StateFlow<List<Client>> = _clientes.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val clientesFiltrados = combine(_clientes, _searchQuery) { clientes, query ->
        if (query.isEmpty()) {
            clientes
        } else {
            clientes.filter { it.nome.contains(query, ignoreCase = true) || it.telefone.contains(query) }
        }
    }

    init {
        carregarDadosMock()
    }

    private fun carregarDadosMock() {
        _clientes.value = MockDataSource.getMockClients()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun getClientById(clientId: String): Client? {
        return _clientes.value.find { it.id == clientId }
    }
}
