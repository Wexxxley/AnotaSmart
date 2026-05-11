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

    private val _mostrarModalNovoCliente = MutableStateFlow(false)
    val mostrarModalNovoCliente: StateFlow<Boolean> = _mostrarModalNovoCliente.asStateFlow()

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

    fun abrirModalNovoCliente() {
        _mostrarModalNovoCliente.value = true
    }

    fun fecharModalNovoCliente() {
        _mostrarModalNovoCliente.value = false
    }

    fun salvarNovoCliente(
        nome: String,
        telefone: String,
        endereco: String?,
        imagePath: String?
    ) {
        val novoCliente = Client(
            id = java.util.UUID.randomUUID().toString(),
            nome = nome,
            telefone = telefone,
            endereco = endereco,
            imagePath = imagePath
        )
        val listaAtual = _clientes.value.toMutableList()
        listaAtual.add(0, novoCliente)
        _clientes.value = listaAtual
        fecharModalNovoCliente()
    }

    fun getClientById(clientId: String): Client? {
        return _clientes.value.find { it.id == clientId }
    }
}
