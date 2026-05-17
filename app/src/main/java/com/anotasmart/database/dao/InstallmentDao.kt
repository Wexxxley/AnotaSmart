package com.anotasmart.database.dao

import androidx.room.*
import com.anotasmart.model.InstallmentStatus
import com.anotasmart.model.InstallmentWithSale
import com.anotasmart.model.entity.Installment
import kotlinx.coroutines.flow.Flow

@Dao
interface InstallmentDao {
    @Query("SELECT * FROM Installment ORDER BY dataVencimento ASC")
    fun getAll(): Flow<List<Installment>>

    @Transaction
    @Query("""
        SELECT i.* FROM Installment i 
        INNER JOIN Sale s ON i.saleId = s.id 
        ORDER BY i.dataVencimento ASC
    """)
    fun getAllWithSale(): Flow<List<InstallmentWithSale>>

    // For simplicity, we can use SaleWithRelations and then flatten it in ViewModel if needed,
    // OR just use a custom query for InstallmentWithClient if we define it as a POJO (not @Entity).
    
    @Query("SELECT * FROM Installment WHERE saleId = :saleId")
    fun getBySaleId(saleId: String): Flow<List<Installment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(installment: Installment): Long

    @Update
    suspend fun update(installment: Installment): Int

    @Query("UPDATE Installment SET statusParcela = :status, dataPagamento = :dataPagamento WHERE id = :id")
    suspend fun updateStatus(id: String, status: InstallmentStatus, dataPagamento: Long?): Int

    @Delete
    suspend fun delete(installment: Installment): Int
}
