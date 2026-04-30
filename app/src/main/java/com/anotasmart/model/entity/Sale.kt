package com.anotasmart.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.anotasmart.model.SaleStatus

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
    @PrimaryKey val id: String,
    val clientId: String?,
    val dataVenda: Long,
    val status: SaleStatus,
    val valorTotal: Double
)
