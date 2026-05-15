package com.anotasmart.database.dao

import androidx.room.*
import com.anotasmart.model.entity.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM Product WHERE isDeleted = 0 ORDER BY nome ASC")
    fun getAll(): Flow<List<Product>>

    @Query("SELECT * FROM Product WHERE id = :id AND isDeleted = 0")
    fun getById(id: String): Product?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(product: Product): Long

    @Update
    fun update(product: Product): Int

    @Delete
    fun delete(product: Product): Int
}
