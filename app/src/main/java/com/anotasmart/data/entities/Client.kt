package com.anotasmart.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "Client")
data class Client(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val nome: String,
    val telefone: String,
    val endereco: String?,
    val imagePath: String?
)
