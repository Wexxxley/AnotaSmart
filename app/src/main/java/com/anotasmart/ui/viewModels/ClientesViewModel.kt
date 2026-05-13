package com.anotasmart.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.anotasmart.database.dao.ClientDao
import com.anotasmart.model.entity.Client
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

class ClientesViewModel(private val clientDao: ClientDao) : ViewModel() {
    val clientes: StateFlow<List<Client>> = clientDao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _mostrarModalNovoCliente = MutableStateFlow(false)
    val mostrarModalNovoCliente: StateFlow<Boolean> = _mostrarModalNovoCliente.asStateFlow()

    val clientesFiltrados = combine(clientes, _searchQuery) { clientes, query ->
        if (query.isEmpty()) {
            clientes
        } else {
            clientes.filter { it.nome.contains(query, ignoreCase = true) || it.telefone.contains(query) }
        }
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
        viewModelScope.launch {
            val novoCliente = Client(
                id = UUID.randomUUID().toString(),
                nome = nome,
                telefone = telefone,
                endereco = endereco,
                imagePath = imagePath
            )
            withContext(Dispatchers.IO) {
                clientDao.insert(novoCliente)
            }
            fecharModalNovoCliente()
        }
    }

    suspend fun getClientById(clientId: String): Client? {
        return withContext(Dispatchers.IO) {
            clientDao.getById(clientId)
        }
    }

    fun deletarCliente(client: Client, onSucesso: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                clientDao.delete(client)
            }
            onSucesso()
        }
    }
}

class ClientesViewModelFactory(private val clientDao: ClientDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClientesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ClientesViewModel(clientDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
