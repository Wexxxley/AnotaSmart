package com.anotasmart.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Client")
data class Client(
    @PrimaryKey val id: String,
    val nome: String,
    val telefone: String,
    val endereco: String?,
    val imagePath: String?
)
