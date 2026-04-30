package com.anotasmart.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.anotasmart.model.InstallmentStatus
import com.anotasmart.model.PaymentMethod

@Entity(
    tableName = "Installment",
    foreignKeys = [
        ForeignKey(
            entity = Sale::class,
            parentColumns = ["id"],
            childColumns = ["saleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["saleId"])]
)
data class Installment(
    @PrimaryKey val id: String,
    val saleId: String,
    val numeroParcela: Int,
    val valor: Double,
    val dataVencimento: Long,
    val dataPagamento: Long?,
    val statusParcela: InstallmentStatus,
    val metodoPagamento: PaymentMethod?
)
