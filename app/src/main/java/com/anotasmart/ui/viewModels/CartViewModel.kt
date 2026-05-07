package com.anotasmart.ui.viewModels

import androidx.lifecycle.ViewModel
import com.anotasmart.model.CartItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CartViewModel : ViewModel() {
    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    val items: StateFlow<List<CartItem>> = _items.asStateFlow()

    val totalItens: StateFlow<Int> = MutableStateFlow(0).apply {}
    
    private val _totalValor = MutableStateFlow(0.0)
    val totalValor: StateFlow<Double> = _totalValor.asStateFlow()

    fun adicionarItem(item: CartItem) {
        _items.update { currentItems ->
            val existingItem = currentItems.find { it.product?.id == item.product?.id && it.product != null }
            if (existingItem != null) {
                currentItems.map {
                    if (it.id == existingItem.id) it.copy(quantidade = it.quantidade + item.quantidade)
                    else it
                }
            } else {
                currentItems + item
            }
        }
        atualizarTotais()
    }

    fun removerItem(itemId: String) {
        _items.update { it.filterNot { item -> item.id == itemId } }
        atualizarTotais()
    }

    fun alterarQuantidade(itemId: String, novaQuantidade: Double) {
        if (novaQuantidade <= 0) {
            removerItem(itemId)
            return
        }
        _items.update { currentItems ->
            currentItems.map {
                if (it.id == itemId) it.copy(quantidade = novaQuantidade)
                else it
            }
        }
        atualizarTotais()
    }

    private fun atualizarTotais() {
        val total = _items.value.sumOf { it.total }
        _totalValor.value = total
    }
    
    fun limparCarrinho() {
        _items.value = emptyList()
        _totalValor.value = 0.0
    }
}
