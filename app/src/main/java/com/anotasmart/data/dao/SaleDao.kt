package com.anotasmart.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.anotasmart.data.entities.Sale
import com.anotasmart.data.entities.SaleWithDetails
import com.anotasmart.data.enums.SaleStatus

@Dao
interface SaleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sale: Sale)

    @Query("UPDATE Sale SET status = :newStatus WHERE id = :saleId")
    suspend fun updateStatus(saleId: String, newStatus: SaleStatus)

    @Query("SELECT * FROM Sale ORDER BY dataVenda DESC LIMIT :limit OFFSET :offset")
    suspend fun getSalesHistoryPaginated(limit: Int, offset: Int): List<Sale>

    @Transaction
    @Query("SELECT * FROM Sale WHERE id = :saleId")
    suspend fun getSaleWithDetails(saleId: String): SaleWithDetails?

    @Query("SELECT * FROM Sale WHERE dataVenda BETWEEN :startDate AND :endDate AND status = 'FINALIZADA' ORDER BY dataVenda DESC")
    suspend fun getProfitabilityReport(startDate: Long, endDate: Long): List<Sale>
}
