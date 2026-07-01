package com.anotasmart.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.anotasmart.database.dao.ExpenseDao
import com.anotasmart.database.dao.InstallmentDao
import com.anotasmart.database.dao.SaleDao
import com.anotasmart.model.PeriodoRelatorio
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.util.*

data class FinancialOverviewState(
    val faturamento: Double = 0.0,
    val lucroEstimado: Double = 0.0,
    val despesas: Double = 0.0,
    val totalVendas: Int = 0,
    val lucroLiquido: Double = 0.0,
    val dinheiroEmCaixa: Double = 0.0,
    val contasAReceber: Double = 0.0,
    val inadimplencia: Double = 0.0
)

class RelatoriosViewModel(
    private val saleDao: SaleDao,
    private val expenseDao: ExpenseDao,
    private val installmentDao: InstallmentDao
) : ViewModel() {

    private val _periodo = MutableStateFlow(PeriodoRelatorio.MES)
    val periodo: StateFlow<PeriodoRelatorio> = _periodo.asStateFlow()

    // flatMapLatest escuta cada mudança em _periodo. Quando muda, o flatMapLatest cancela todas as consultas em andamento e inicia as novas.
    @OptIn(ExperimentalCoroutinesApi::class)
    val financialOverview: StateFlow<FinancialOverviewState> = _periodo.flatMapLatest { p ->
        val range = getRangeFromPeriodo(p)
        val now = System.currentTimeMillis()

        // Combine orquestrar múltiplas chamadas assíncronas ao db
        val combine = combine(
            saleDao.getTotalRevenue(range.first, range.second),
            saleDao.getEstimatedProfit(range.first, range.second),
            expenseDao.getTotalExpenses(range.first, range.second),
            saleDao.getSalesCount(range.first, range.second),
            installmentDao.getPaidAmount(range.first, range.second),
            installmentDao.getTotalReceivables(),
            installmentDao.getOverdueAmount(now)
        ) { values ->
            val rev = values[0] as? Double ?: 0.0
            val profit = values[1] as? Double ?: 0.0
            val exp = values[2] as? Double ?: 0.0
            val count = values[3] as? Int ?: 0
            val paid = values[4] as? Double ?: 0.0
            val receivables = values[5] as? Double ?: 0.0
            val overdue = values[6] as? Double ?: 0.0

            FinancialOverviewState(
                faturamento = rev,
                lucroEstimado = profit,
                despesas = exp,
                totalVendas = count,
                lucroLiquido = profit - exp,
                dinheiroEmCaixa = paid,
                contasAReceber = receivables,
                inadimplencia = overdue
            )
        }
        combine //Retorno
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FinancialOverviewState())

    fun setPeriodo(p: PeriodoRelatorio) {
        _periodo.value = p
    }

    //Cálculo do intervalo temporal. Retorna o intervalo em milissegundos contendo a data inicial e a data final
    private fun getRangeFromPeriodo(p: PeriodoRelatorio): Pair<Long, Long> {
        val calendario = Calendar.getInstance()
        val now = calendario.timeInMillis
        
        return when (p) {
            // Se for HOJE, zera horas, minutos e segundos. Retornando o milissegundo inicial do dia até o momento atual
            PeriodoRelatorio.HOJE -> {
                calendario.set(Calendar.HOUR_OF_DAY, 0)
                calendario.set(Calendar.MINUTE, 0)
                calendario.set(Calendar.SECOND, 0)
                calendario.set(Calendar.MILLISECOND, 0)
                calendario.timeInMillis to now
            }
            PeriodoRelatorio.SEMANA -> {
                calendario.set(Calendar.DAY_OF_WEEK, calendario.firstDayOfWeek)
                calendario.set(Calendar.HOUR_OF_DAY, 0)
                calendario.timeInMillis to now
            }
            PeriodoRelatorio.MES -> {
                calendario.set(Calendar.DAY_OF_MONTH, 1)
                calendario.set(Calendar.HOUR_OF_DAY, 0)
                calendario.timeInMillis to now
            }
            PeriodoRelatorio.ANO -> {
                calendario.set(Calendar.DAY_OF_YEAR, 1)
                calendario.set(Calendar.HOUR_OF_DAY, 0)
                calendario.timeInMillis to now
            }
            PeriodoRelatorio.TUDO -> 0L to now // Do início (tempo zero) até agora.
        }
    }
}

class RelatoriosViewModelFactory(
    private val saleDao: SaleDao,
    private val expenseDao: ExpenseDao,
    private val installmentDao: InstallmentDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RelatoriosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RelatoriosViewModel(saleDao, expenseDao, installmentDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
