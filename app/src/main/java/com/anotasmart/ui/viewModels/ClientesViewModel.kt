package com.anotasmart.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.anotasmart.database.dao.ClientDao
import com.anotasmart.database.dao.InstallmentDao
import com.anotasmart.database.dao.SaleDao
import com.anotasmart.model.InstallmentStatus
import com.anotasmart.model.SaleWithRelations
import com.anotasmart.model.entity.Client
import com.anotasmart.model.entity.Installment
import com.anotasmart.model.entity.Sale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class ClientesViewModel(
    private val clientDao: ClientDao,
    private val saleDao: SaleDao,
    private val installmentDao: InstallmentDao
) : ViewModel() {
    val clientes: StateFlow<List<Client>> = clientDao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedClientId = MutableStateFlow<String?>(null)

    // Todas as vendas com relações, que usaremos para filtrar por cliente
    private val _vendasRaw = saleDao.getAllWithRelations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Vendas filtradas para o cliente selecionado
    val vendasCliente = combine(_vendasRaw, _selectedClientId) { vendas, clientId ->
        if (clientId == null) emptyList()
        else vendas.filter { it.sale.clientId == clientId }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Recebíveis (parcelas pendentes) filtrados para o cliente selecionado
    val recebiveisCliente = vendasCliente.map { sales ->
        sales.flatMap { saleWithRelations ->
            saleWithRelations.installments
                .filter { it.statusParcela != InstallmentStatus.PAGA }
                .map { Triple(it, saleWithRelations, saleWithRelations.client) }
        }.sortedBy { it.first.dataVencimento }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSelectedClient(clientId: String?) {
        _selectedClientId.value = clientId
    }

    fun marcarComoPaga(installmentId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val targetSale = _vendasRaw.value.find { relations ->
                    relations.installments.any { it.id == installmentId }
                }

                if (targetSale != null) {
                    installmentDao.updateStatus(
                        id = installmentId,
                        status = InstallmentStatus.PAGA,
                        dataPagamento = System.currentTimeMillis()
                    )

                    val updatedInstallments = targetSale.installments.map {
                        if (it.id == installmentId) it.copy(statusParcela = InstallmentStatus.PAGA) else it
                    }

                    if (updatedInstallments.all { it.statusParcela == InstallmentStatus.PAGA }) {
                        val updatedSale = targetSale.sale.copy(status = com.anotasmart.model.SaleStatus.FINALIZADA)
                        saleDao.updateSale(updatedSale)
                    }
                }
            }
        }
    }

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

class ClientesViewModelFactory(
    private val clientDao: ClientDao,
    private val saleDao: SaleDao,
    private val installmentDao: InstallmentDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClientesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ClientesViewModel(clientDao, saleDao, installmentDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
