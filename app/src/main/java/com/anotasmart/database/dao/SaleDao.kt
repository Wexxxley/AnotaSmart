package com.anotasmart.database.dao

import androidx.room.*
import com.anotasmart.model.entity.Installment
import com.anotasmart.model.entity.Sale
import com.anotasmart.model.entity.SaleItem

@Dao
interface SaleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSale(sale: Sale): Long

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
}
