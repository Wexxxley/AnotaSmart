package com.anotasmart.database.dao

import androidx.room.*
import com.anotasmart.model.entity.CartItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartItemDao {
    @Query("SELECT * FROM CartItem")
    fun getAll(): Flow<List<CartItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(cartItem: CartItemEntity): Long

    @Update
    fun update(cartItem: CartItemEntity): Int

    @Delete
    fun delete(cartItem: CartItemEntity): Int

    @Query("DELETE FROM CartItem")
    fun deleteAll()

    @Query("SELECT * FROM CartItem WHERE id = :id")
    fun getById(id: String): CartItemEntity?
}
