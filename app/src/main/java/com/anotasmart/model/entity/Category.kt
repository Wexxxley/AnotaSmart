package com.anotasmart.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.anotasmart.model.CategoryType

@Entity(tableName = "Category")
data class Category(
    @PrimaryKey val id: String,
    val nome: String,
    val tipo: CategoryType
)
