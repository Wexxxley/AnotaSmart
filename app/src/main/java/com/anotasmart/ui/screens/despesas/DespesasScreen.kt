package com.anotasmart.ui.screens.despesas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anotasmart.AnotaSmartApplication
import com.anotasmart.model.entity.Expense
import com.anotasmart.ui.components.StandardItemCard
import com.anotasmart.ui.components.expandableGroup
import com.anotasmart.ui.screens.despesas.components.DialogNovaDespesa
import com.anotasmart.ui.viewModels.CategoriasViewModel
import com.anotasmart.ui.viewModels.CategoriasViewModelFactory
import com.anotasmart.ui.viewModels.DespesasViewModel
import com.anotasmart.ui.viewModels.DespesasViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DespesasScreen() {
    val context = LocalContext.current
    val database = (context.applicationContext as AnotaSmartApplication).database
    val viewModel: DespesasViewModel = viewModel(
        factory = DespesasViewModelFactory(database.expenseDao())
    )
    val categoriasViewModel: CategoriasViewModel = viewModel(
        factory = CategoriasViewModelFactory(database.categoryDao())
    )
    val expenses by viewModel.expenses.collectAsState()
    val categories by categoriasViewModel.categoriasDespesas.collectAsState(initial = emptyList())
    val showAddModal by viewModel.showAddModal.collectAsState()

    val groupedExpenses = remember(expenses) { viewModel.getGroupedExpenses(expenses) }
    val expandedStates = remember { mutableStateMapOf<String, Boolean>() }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            groupedExpenses.forEach { (monthYear, monthExpenses) ->
                val isExpanded = expandedStates[monthYear] ?: true
                val subtotal = viewModel.getMonthSubtotal(monthYear, groupedExpenses)

                expandableGroup(
                    title = monthYear,
                    subtitle = "Subtotal: R$ ${String.format("%.2f", subtotal)}",
                    items = monthExpenses,
                    isExpanded = isExpanded,
                    onToggle = { expandedStates[monthYear] = !isExpanded },
                    key = { it.id }
                ) { expense ->
                    ExpenseCard(
                        expense = expense,
                        categoryName = categories.find { it.id == expense.categoryId }?.nome ?: "Sem categoria"
                    )
                }
            }
        }

        // FAB
        ExtendedFloatingActionButton(
            onClick = { viewModel.openAddModal() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            icon = { Icon(Icons.Default.Add, contentDescription = null) },
            text = { Text("Cadastrar despesa") }
        )

        if (showAddModal) {
            DialogNovaDespesa(
                categorias = categories,
                onDismissRequest = { viewModel.closeAddModal() },
                onConfirmar = { desc, valor, catId, data ->
                    viewModel.saveExpense(desc, valor, catId, data)
                },
                onNovaCategoria = { nome, tipo ->
                    categoriasViewModel.salvarNovaCategoria(nome, tipo)
                }
            )
        }
    }
}

@Composable
fun ExpenseCard(expense: Expense, categoryName: String) {
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR")) }

    StandardItemCard(
        title = expense.description,
        label = "${categoryName} • ${dateFormatter.format(Date(expense.date))}",
        value = "R$ ${String.format("%.2f", expense.amount)}",
        valueColor = MaterialTheme.colorScheme.error,
        backgroundColor = MaterialTheme.colorScheme.surfaceContainerLowest
    )
}
