package com.anotasmart.ui.screens.pedidos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.anotasmart.database.dao.InstallmentDao
import com.anotasmart.database.dao.SaleDao
import com.anotasmart.model.InstallmentStatus
import com.anotasmart.model.SaleStatus
import com.anotasmart.model.SaleWithRelations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PedidosViewModel(
    private val saleDao: SaleDao,
    private val installmentDao: InstallmentDao
) : ViewModel() {

    val vendas: StateFlow<List<SaleWithRelations>> = saleDao.getAllWithRelations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // "Recebíveis" são todas as parcelas pendentes ou atrasadas de todas as vendas
    val recebiveis = vendas.map { sales ->
        sales.flatMap { saleWithRelations ->
            saleWithRelations.installments
                .filter { it.statusParcela != InstallmentStatus.PAGA }
                .map { installment ->
                    // Criamos um objeto temporário para exibir na lista de recebíveis
                    // que contém a parcela, a venda com relações e o cliente
                    Triple(installment, saleWithRelations, saleWithRelations.client)
                }
        }.sortedBy { it.first.dataVencimento }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalAReceber = recebiveis.map { list ->
        list.sumOf { it.first.valor }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalAtrasado = recebiveis.map { list ->
        val agora = System.currentTimeMillis()
        list.filter { it.first.dataVencimento < agora }.sumOf { it.first.valor }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun marcarComoPaga(installmentId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val salesWithRelations = saleDao.getAllWithRelations().first()
                val targetSale = salesWithRelations.find { relations ->
                    relations.installments.any { it.id == installmentId }
                }

                if (targetSale != null) {
                    installmentDao.updateStatus(
                        id = installmentId,
                        status = InstallmentStatus.PAGA,
                        dataPagamento = System.currentTimeMillis()
                    )

                    val updatedInstallments = targetSale.installments.map {
                        if (it.id == installmentId) it.copy(statusParcela = InstallmentStatus.PAGA) else it
                    }

                    if (updatedInstallments.all { it.statusParcela == InstallmentStatus.PAGA }) {
                        val updatedSale = targetSale.sale.copy(status = SaleStatus.FINALIZADA)
                        saleDao.updateSale(updatedSale)
                    }
                }
            }
        }
    }
}

class PedidosViewModelFactory(
    private val saleDao: SaleDao,
    private val installmentDao: InstallmentDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PedidosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PedidosViewModel(saleDao, installmentDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
