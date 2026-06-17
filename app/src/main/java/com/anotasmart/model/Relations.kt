package com.anotasmart.model

import androidx.room.Embedded
import androidx.room.Relation
import com.anotasmart.model.entity.Client
import com.anotasmart.model.entity.Installment
import com.anotasmart.model.entity.Product
import com.anotasmart.model.entity.Sale
import com.anotasmart.model.entity.SaleItem

data class SaleWithClient(
    @Embedded val sale: Sale,
    @Relation(
        parentColumn = "clientId",
        entityColumn = "id"
    )
    val client: Client?
)

data class SaleWithRelations(
    @Embedded val sale: Sale,
    @Relation(
        parentColumn = "clientId",
        entityColumn = "id"
    )
    val client: Client?,
    @Relation(
        parentColumn = "id",
        entityColumn = "saleId"
    )
    val installments: List<Installment>,
    @Relation(
        entity = SaleItem::class,
        parentColumn = "id",
        entityColumn = "saleId"
    )
    val items: List<SaleItemWithProduct>
)

data class SaleItemWithProduct(
    @Embedded val saleItem: SaleItem,
    @Relation(
        parentColumn = "productId",
        entityColumn = "id"
    )
    val product: Product?
)

data class InstallmentWithSale(
    @Embedded val installment: Installment,
    @Relation(
        parentColumn = "saleId",
        entityColumn = "id"
    )
    val sale: Sale
)

data class InstallmentWithClient(
    @Embedded val installment: Installment,
    @Relation(
        parentColumn = "saleId",
        entityColumn = "id"
    )
    val sale: Sale
)
