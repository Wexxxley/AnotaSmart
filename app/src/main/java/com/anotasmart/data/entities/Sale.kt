package com.anotasmart.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.anotasmart.data.enums.SaleStatus
import java.util.UUID

@Entity(
    tableName = "Sale",
    foreignKeys = [
        ForeignKey(
            entity = Client::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["clientId"])]
)
data class Sale(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val clientId: String?,
    val dataVenda: Long,
    val status: SaleStatus,
    val valorTotal: Double
)
