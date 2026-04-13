package com.anotasmart.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.anotasmart.data.entities.Installment
import com.anotasmart.data.enums.InstallmentStatus
import com.anotasmart.data.enums.PaymentMethod

@Dao
interface InstallmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(installments: List<Installment>)

    @Query("UPDATE Installment SET dataPagamento = :paymentDate, metodoPagamento = :method, statusParcela = :status WHERE id = :id")
    suspend fun updatePayment(id: String, paymentDate: Long, method: PaymentMethod, status: InstallmentStatus)

    @Query("""
        SELECT i.* FROM Installment i 
        INNER JOIN Sale s ON i.saleId = s.id 
        WHERE s.clientId = :clientId AND i.statusParcela != 'PAGA'
    """)
    suspend fun getPendingByClientId(clientId: String): List<Installment>

    @Query("""
        SELECT SUM(i.valor) FROM Installment i 
        INNER JOIN Sale s ON i.saleId = s.id 
        WHERE s.clientId = :clientId AND i.statusParcela != 'PAGA'
    """)
    suspend fun getTotalDebtByClientId(clientId: String): Double

    @Query("SELECT COUNT(*) FROM Installment WHERE saleId = :saleId AND statusParcela != 'PAGA'")
    suspend fun countPendingBySaleId(saleId: String): Int
}
