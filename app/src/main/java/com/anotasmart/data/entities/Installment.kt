package com.anotasmart.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.anotasmart.data.enums.InstallmentStatus
import com.anotasmart.data.enums.PaymentMethod
import java.util.UUID

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
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val saleId: String,
    val numeroParcela: Int,
    val valor: Double,
    val dataVencimento: Long,
    val dataPagamento: Long?,
    val statusParcela: InstallmentStatus,
    val metodoPagamento: PaymentMethod?
)
