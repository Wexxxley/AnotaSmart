package com.anotasmart.ui.screens.despesas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.anotasmart.database.dao.ExpenseDao
import com.anotasmart.model.entity.Expense
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class DespesasViewModel(private val expenseDao: ExpenseDao) : ViewModel() {
    val expenses: StateFlow<List<Expense>> = expenseDao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _showAddModal = MutableStateFlow(false)
    val showAddModal: StateFlow<Boolean> = _showAddModal.asStateFlow()

    fun openAddModal() {
        _showAddModal.value = true
    }

    fun closeAddModal() {
        _showAddModal.value = false
    }

    fun saveExpense(description: String, amount: Double, categoryId: String, date: Long) {
        viewModelScope.launch {
            val newExpense = Expense(
                id = UUID.randomUUID().toString(),
                categoryId = categoryId,
                description = description,
                amount = amount,
                date = date
            )
            withContext(Dispatchers.IO) {
                expenseDao.insert(newExpense)
            }
            closeAddModal()
        }
    }

    fun getGroupedExpenses(expenseList: List<Expense>): Map<String, List<Expense>> {
        val calendar = Calendar.getInstance()
        val locale = Locale.forLanguageTag("pt-BR")
        return expenseList
            .sortedByDescending { it.date }
            .groupBy { expense ->
                calendar.timeInMillis = expense.date
                val month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, locale)
                val year = calendar.get(Calendar.YEAR)
                val monthName = month?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() } ?: "Desconhecido"
                "$monthName $year"
            }
    }

    fun getMonthSubtotal(monthYear: String, grouped: Map<String, List<Expense>>): Double {
        return grouped[monthYear]?.sumOf { it.amount } ?: 0.0
    }
}

class DespesasViewModelFactory(private val expenseDao: ExpenseDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DespesasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DespesasViewModel(expenseDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
