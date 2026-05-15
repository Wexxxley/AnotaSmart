package com.anotasmart.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.anotasmart.database.dao.CartItemDao
import com.anotasmart.database.dao.ProductDao
import com.anotasmart.model.CartItem
import com.anotasmart.model.entity.CartItemEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartViewModel(
    private val cartItemDao: CartItemDao,
    private val productDao: ProductDao
) : ViewModel() {

    // Transformamos as entidades do banco de volta para o modelo de UI CartItem
    val items: StateFlow<List<CartItem>> = cartItemDao.getAll()
        .map { entities ->
            entities.map { entity ->
                val product = entity.productId?.let { id ->
                    withContext(Dispatchers.IO) { productDao.getById(id) }
                }
                CartItem(
                    id = entity.id,
                    product = product,
                    nome = entity.nome,
                    precoVenda = entity.precoVenda,
                    precoCusto = entity.precoCusto,
                    quantidade = entity.quantidade,
                    unidadeMedida = entity.unidadeMedida
                )
            }
        }
        .flowOn(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalValor: StateFlow<Double> = items.map { list ->
        list.sumOf { it.total }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun adicionarItem(item: CartItem) {
        viewModelScope.launch {
            val currentItems = items.value
            val existingItem = currentItems.find { it.product?.id == item.product?.id && it.product != null }
            
            if (existingItem != null) {
                val updatedEntity = CartItemEntity(
                    id = existingItem.id,
                    productId = existingItem.product?.id,
                    nome = existingItem.nome,
                    precoVenda = existingItem.precoVenda,
                    precoCusto = existingItem.precoCusto,
                    quantidade = existingItem.quantidade + item.quantidade,
                    unidadeMedida = existingItem.unidadeMedida
                )
                withContext(Dispatchers.IO) {
                    cartItemDao.update(updatedEntity)
                }
            } else {
                val newEntity = CartItemEntity(
                    id = item.id,
                    productId = item.product?.id,
                    nome = item.nome,
                    precoVenda = item.precoVenda,
                    precoCusto = item.precoCusto,
                    quantidade = item.quantidade,
                    unidadeMedida = item.unidadeMedida
                )
                withContext(Dispatchers.IO) {
                    cartItemDao.insert(newEntity)
                }
            }
        }
    }

    fun removerItem(itemId: String) {
        viewModelScope.launch {
            val item = withContext(Dispatchers.IO) { cartItemDao.getById(itemId) }
            if (item != null) {
                withContext(Dispatchers.IO) {
                    cartItemDao.delete(item)
                }
            }
        }
    }

    fun alterarQuantidade(itemId: String, novaQuantidade: Double) {
        if (novaQuantidade <= 0) {
            removerItem(itemId)
            return
        }
        viewModelScope.launch {
            val entity = withContext(Dispatchers.IO) { cartItemDao.getById(itemId) }
            if (entity != null) {
                val updated = entity.copy(quantidade = novaQuantidade)
                withContext(Dispatchers.IO) {
                    cartItemDao.update(updated)
                }
            }
        }
    }

    fun limparCarrinho() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                cartItemDao.deleteAll()
            }
        }
    }
}

class CartViewModelFactory(
    private val cartItemDao: CartItemDao,
    private val productDao: ProductDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartViewModel(cartItemDao, productDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
