package com.anotasmart.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.anotasmart.data.enums.CategoryType
import java.util.UUID

@Entity(tableName = "Category")
data class Category(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val nome: String,
    val tipo: CategoryType
)
