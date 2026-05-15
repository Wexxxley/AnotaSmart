package com.anotasmart.database.dao

import androidx.room.*
import com.anotasmart.model.entity.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM Category")
    fun getAll(): Flow<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(category: Category): Long

    @Update
    fun update(category: Category): Int

    @Delete
    fun delete(category: Category): Int

    @Query("SELECT * FROM Category WHERE id = :id")
    fun getById(id: String): Category?

    @Query("SELECT COUNT(*) FROM Product WHERE categoryId = :categoryId AND isDeleted = 0")
    suspend fun countProductsByCategory(categoryId: String): Int

    @Query("SELECT COUNT(*) FROM Expense WHERE categoryId = :categoryId")
    suspend fun countExpensesByCategory(categoryId: String): Int
}
