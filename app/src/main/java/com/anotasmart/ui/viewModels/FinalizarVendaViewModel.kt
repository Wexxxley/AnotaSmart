package com.anotasmart.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.anotasmart.database.dao.ClientDao
import com.anotasmart.model.PaymentMethod
import com.anotasmart.model.entity.Client
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class FinalizarVendaViewModel(private val clientDao: ClientDao) : ViewModel() {
    private val _clientes = clientDao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val clientesFiltrados = combine(_clientes, _searchQuery) { clientes, query ->
        if (query.isBlank()) clientes
        else clientes.filter { it.nome.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _clienteSelecionado = MutableStateFlow<Client?>(null)
    val clienteSelecionado: StateFlow<Client?> = _clienteSelecionado.asStateFlow()

    private val _metodoPagamento = MutableStateFlow<PaymentMethod?>(null)
    val metodoPagamento: StateFlow<PaymentMethod?> = _metodoPagamento.asStateFlow()

    fun selecionarCliente(cliente: Client?) {
        _clienteSelecionado.value = cliente
    }

    fun selecionarMetodoPagamento(metodo: PaymentMethod) {
        _metodoPagamento.value = metodo
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
}

class FinalizarVendaViewModelFactory(private val clientDao: ClientDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FinalizarVendaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FinalizarVendaViewModel(clientDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
