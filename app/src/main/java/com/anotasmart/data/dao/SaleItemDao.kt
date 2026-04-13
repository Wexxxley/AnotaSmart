package com.anotasmart.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.anotasmart.data.entities.SaleItem

@Dao
interface SaleItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<SaleItem>)

    @Query("SELECT * FROM SaleItem WHERE saleId = :saleId")
    suspend fun getBySaleId(saleId: String): List<SaleItem>
}
