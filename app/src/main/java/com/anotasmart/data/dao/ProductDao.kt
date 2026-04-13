package com.anotasmart.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.anotasmart.data.entities.Product

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product)

    @Update
    suspend fun update(product: Product)

    @Query("SELECT * FROM Product WHERE id = :id")
    suspend fun getById(id: String): Product?

    @Query("SELECT * FROM Product ORDER BY nome ASC LIMIT :limit OFFSET :offset")
    suspend fun getAllPaginated(limit: Int, offset: Int): List<Product>

    @Query("SELECT * FROM Product WHERE categoryId = :categoryId ORDER BY nome ASC LIMIT :limit OFFSET :offset")
    suspend fun getByCategoryIdPaginated(categoryId: String, limit: Int, offset: Int): List<Product>

    @Query("UPDATE Product SET quantidadeEstoque = :newStock, precoCusto = :newCost WHERE id = :id")
    suspend fun updateStockAndCost(id: String, newStock: Double, newCost: Double)
}
