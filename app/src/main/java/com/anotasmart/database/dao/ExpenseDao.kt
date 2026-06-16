package com.anotasmart.database.dao

import androidx.room.*
import com.anotasmart.model.entity.Expense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM Expense")
    fun getAll(): Flow<List<Expense>>

    @Query("SELECT * FROM Expense WHERE id = :id")
    fun getById(id: String): Expense?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(expense: Expense): Long

    @Update
    fun update(expense: Expense): Int

    @Delete
    fun delete(expense: Expense): Int

    @Query("SELECT * FROM Expense WHERE categoryId = :categoryId")
    fun getByCategory(categoryId: String): Flow<List<Expense>>

    @Query("SELECT SUM(amount) FROM Expense WHERE date BETWEEN :startDate AND :endDate")
    fun getTotalExpenses(startDate: Long, endDate: Long): Flow<Double?>
}
