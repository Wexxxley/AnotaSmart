package com.anotasmart.database.dao

import androidx.room.*
import com.anotasmart.model.SaleWithRelations
import com.anotasmart.model.entity.Installment
import com.anotasmart.model.entity.Sale
import com.anotasmart.model.entity.SaleItem
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {
    @Transaction
    @Query("SELECT * FROM Sale ORDER BY dataVenda DESC")
    fun getAllWithRelations(): Flow<List<SaleWithRelations>>

    @Transaction
    @Query("SELECT * FROM Sale WHERE id = :id")
    fun getByIdWithRelations(id: String): Flow<SaleWithRelations?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSale(sale: Sale): Long

    @Update
    suspend fun updateSale(sale: Sale): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSaleItems(items: List<SaleItem>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInstallments(installments: List<Installment>): List<Long>

    @Transaction
    suspend fun completeSale(
        sale: Sale,
        items: List<SaleItem>,
        installments: List<Installment>
    ): Int {
        insertSale(sale)
        insertSaleItems(items)
        insertInstallments(installments)
        return 1
    }

    @Query("SELECT SUM(valorTotal) FROM Sale WHERE dataVenda BETWEEN :startDate AND :endDate AND status != 'CANCELADA'")
    fun getTotalRevenue(startDate: Long, endDate: Long): Flow<Double?>

    @Query("""
        SELECT SUM((si.precoVendaNoAto - si.custoUnitarioNoAto) * si.quantidade) 
        FROM SaleItem si 
        INNER JOIN Sale s ON si.saleId = s.id 
        WHERE s.dataVenda BETWEEN :startDate AND :endDate AND s.status != 'CANCELADA'
    """)
    fun getEstimatedProfit(startDate: Long, endDate: Long): Flow<Double?>

    @Query("SELECT COUNT(*) FROM Sale WHERE dataVenda BETWEEN :startDate AND :endDate AND status != 'CANCELADA'")
    fun getSalesCount(startDate: Long, endDate: Long): Flow<Int>
}
