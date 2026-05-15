package com.anotasmart.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.anotasmart.database.dao.CartItemDao
import com.anotasmart.database.dao.ClientDao
import com.anotasmart.database.dao.ProductDao
import com.anotasmart.database.dao.SaleDao
import com.anotasmart.model.InstallmentStatus
import com.anotasmart.model.PaymentMethod
import com.anotasmart.model.SaleStatus
import com.anotasmart.model.entity.Client
import com.anotasmart.model.entity.Installment
import com.anotasmart.model.entity.Sale
import com.anotasmart.model.entity.SaleItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class FinalizarVendaViewModel(
    private val clientDao: ClientDao,
    private val cartItemDao: CartItemDao,
    private val saleDao: SaleDao,
    private val productDao: ProductDao
) : ViewModel() {
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

    private val _cartItems = cartItemDao.getAll()
    
    val totalVenda = _cartItems.map { items ->
        items.sumOf { it.precoVenda * it.quantidade }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun selecionarCliente(cliente: Client?) {
        _clienteSelecionado.value = cliente
    }

    fun selecionarMetodoPagamento(metodo: PaymentMethod) {
        _metodoPagamento.value = metodo
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun confirmarVendaAVista(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val items = _cartItems.first()
            if (items.isEmpty()) return@launch

            val total = totalVenda.value
            val saleId = UUID.randomUUID().toString()
            val agora = System.currentTimeMillis()

            val sale = Sale(
                id = saleId,
                clientId = _clienteSelecionado.value?.id,
                dataVenda = agora,
                status = SaleStatus.FINALIZADA,
                valorTotal = total
            )

            val saleItems = items.map { cartItem ->
                SaleItem(
                    id = UUID.randomUUID().toString(),
                    saleId = saleId,
                    productId = cartItem.productId,
                    nomeCustomizado = if (cartItem.productId == null) cartItem.nome else null,
                    quantidade = cartItem.quantidade,
                    custoUnitarioNoAto = cartItem.precoCusto ?: 0.0,
                    precoVendaNoAto = cartItem.precoVenda
                )
            }

            val installment = Installment(
                id = UUID.randomUUID().toString(),
                saleId = saleId,
                numeroParcela = 1,
                valor = total,
                dataVencimento = agora,
                dataPagamento = agora,
                statusParcela = InstallmentStatus.PAGA,
                metodoPagamento = _metodoPagamento.value
            )

            withContext(Dispatchers.IO) {
                // Persistir venda
                saleDao.completeSale(sale, saleItems, listOf(installment))

                // Atualizar estoque
                items.forEach { cartItem ->
                    if (cartItem.productId != null) {
                        val product = productDao.getById(cartItem.productId)
                        if (product != null) {
                            productDao.update(product.copy(quantidadeEstoque = product.quantidadeEstoque - cartItem.quantidade))
                        }
                    }
                }

                // Limpar carrinho
                cartItemDao.deleteAll()
            }

            onSuccess()
        }
    }
}

class FinalizarVendaViewModelFactory(
    private val clientDao: ClientDao,
    private val cartItemDao: CartItemDao,
    private val saleDao: SaleDao,
    private val productDao: ProductDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FinalizarVendaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FinalizarVendaViewModel(clientDao, cartItemDao, saleDao, productDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
