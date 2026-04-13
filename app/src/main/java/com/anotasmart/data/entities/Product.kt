package com.anotasmart.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.anotasmart.data.enums.ItemType
import com.anotasmart.data.enums.UnitType
import java.util.UUID

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
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val categoryId: String?,
    val nome: String,
    val precoCusto: Double,
    val precoVenda: Double,
    val unidadeMedida: UnitType,
    val tipoItem: ItemType,
    val quantidadeEstoque: Double,
    val imagePath: String?
)
