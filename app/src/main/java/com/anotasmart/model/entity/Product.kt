package com.anotasmart.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.anotasmart.model.ItemType
import com.anotasmart.model.UnitType

@Entity(
    tableName = "Product",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["categoryId"])]
)
data class Product(
    @PrimaryKey val id: String,
    val categoryId: String?,
    val nome: String,
    val precoCusto: Double,
    val precoVenda: Double,
    val unidadeMedida: UnitType,
    val tipoItem: ItemType,
    val quantidadeEstoque: Double,
    val imagePath: String?,
    val isDeleted: Boolean = false
)
