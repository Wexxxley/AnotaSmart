package com.anotasmart.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.anotasmart.model.UnitType

@Entity(tableName = "CartItem")
data class CartItemEntity(
    @PrimaryKey val id: String,
    val productId: String?,
    val nome: String,
    val precoVenda: Double,
    val precoCusto: Double?,
    val quantidade: Double,
    val unidadeMedida: UnitType
)
