package com.anotasmart.data.entities

import androidx.room.Embedded
import androidx.room.Relation

data class SaleWithDetails(
    @Embedded val sale: Sale,
    @Relation(
        parentColumn = "id",
        entityColumn = "saleId"
    )
    val items: List<SaleItem>,
    @Relation(
        parentColumn = "id",
        entityColumn = "saleId"
    )
    val installments: List<Installment>
)
