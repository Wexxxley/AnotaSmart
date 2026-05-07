package com.anotasmart.model

import com.anotasmart.model.entity.Product
import java.util.UUID

data class CartItem(
    val id: String = UUID.randomUUID().toString(),
    val product: Product? = null, // null para itens avulsos
    val nome: String,
    val precoVenda: Double,
    val precoCusto: Double? = null,
    val quantidade: Double,
    val unidadeMedida: UnitType = UnitType.UN
) {
    val total: Double get() = precoVenda * quantidade
}
