package com.anotasmart.ui.viewModels

import androidx.lifecycle.ViewModel
import com.anotasmart.data.mocks.MockDataSource
import com.anotasmart.model.CategoryType
import com.anotasmart.model.entity.Category
import com.anotasmart.model.entity.Expense
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

class DespesasViewModel : ViewModel() {
    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()

    private val _showAddModal = MutableStateFlow(false)
    val showAddModal: StateFlow<Boolean> = _showAddModal.asStateFlow()

    init {
        loadMockData()
    }

    private fun loadMockData() {
        _expenses.value = MockDataSource.getMockExpenses()
    }

    fun openAddModal() {
        _showAddModal.value = true
    }

    fun closeAddModal() {
        _showAddModal.value = false
    }

    fun saveExpense(description: String, amount: Double, categoryId: String, date: Long) {
        val newExpense = Expense(
            id = UUID.randomUUID().toString(),
            categoryId = categoryId,
            description = description,
            amount = amount,
            date = date
        )
        val currentList = _expenses.value.toMutableList()
        currentList.add(0, newExpense)
        _expenses.value = currentList
        closeAddModal()
    }

    fun getGroupedExpenses(): Map<String, List<Expense>> {
        val calendar = Calendar.getInstance()
        val locale = Locale.forLanguageTag("pt-BR")
        return _expenses.value
            .sortedByDescending { it.date }
            .groupBy { expense ->
                calendar.timeInMillis = expense.date
                val month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, locale)
                val year = calendar.get(Calendar.YEAR)
                val monthName = month?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() } ?: "Desconhecido"
                "$monthName $year"
            }
    }

    fun getMonthSubtotal(monthYear: String): Double {
        val grouped = getGroupedExpenses()
        return grouped[monthYear]?.sumOf { it.amount } ?: 0.0
    }
}
