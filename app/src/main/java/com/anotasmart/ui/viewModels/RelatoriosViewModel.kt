package com.anotasmart.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.anotasmart.database.dao.ExpenseDao
import com.anotasmart.database.dao.SaleDao
import kotlinx.coroutines.flow.*
import java.util.*

enum class PeriodoRelatorio {
    HOJE, SEMANA, MES, ANO, TUDO
}

data class FinancialOverviewState(
    val faturamento: Double = 0.0,
    val lucroEstimado: Double = 0.0,
    val despesas: Double = 0.0,
    val totalVendas: Int = 0,
    val lucroLiquido: Double = 0.0
)

class RelatoriosViewModel(
    private val saleDao: SaleDao,
    private val expenseDao: ExpenseDao
) : ViewModel() {

    private val _periodo = MutableStateFlow(PeriodoRelatorio.MES)
    val periodo: StateFlow<PeriodoRelatorio> = _periodo.asStateFlow()

    val financialOverview: StateFlow<FinancialOverviewState> = _periodo.flatMapLatest { p ->
        val range = getRangeFromPeriodo(p)
        combine(
            saleDao.getTotalRevenue(range.first, range.second),
            saleDao.getEstimatedProfit(range.first, range.second),
            expenseDao.getTotalExpenses(range.first, range.second),
            saleDao.getSalesCount(range.first, range.second)
        ) { rev, profit, exp, count ->
            val revenue = rev ?: 0.0
            val estProfit = profit ?: 0.0
            val expenses = exp ?: 0.0
            FinancialOverviewState(
                faturamento = revenue,
                lucroEstimado = estProfit,
                despesas = expenses,
                totalVendas = count,
                lucroLiquido = estProfit - expenses
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FinancialOverviewState())

    fun setPeriodo(p: PeriodoRelatorio) {
        _periodo.value = p
    }

    private fun getRangeFromPeriodo(p: PeriodoRelatorio): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        val now = cal.timeInMillis
        
        return when (p) {
            PeriodoRelatorio.HOJE -> {
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                cal.timeInMillis to now
            }
            PeriodoRelatorio.SEMANA -> {
                cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.timeInMillis to now
            }
            PeriodoRelatorio.MES -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.timeInMillis to now
            }
            PeriodoRelatorio.ANO -> {
                cal.set(Calendar.DAY_OF_YEAR, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.timeInMillis to now
            }
            PeriodoRelatorio.TUDO -> 0L to now
        }
    }
}

class RelatoriosViewModelFactory(
    private val saleDao: SaleDao,
    private val expenseDao: ExpenseDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RelatoriosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RelatoriosViewModel(saleDao, expenseDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
