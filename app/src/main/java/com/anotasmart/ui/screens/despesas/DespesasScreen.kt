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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anotasmart.model.entity.Expense
import com.anotasmart.ui.screens.despesas.components.DialogNovaDespesa
import com.anotasmart.ui.viewModels.CategoriasViewModel
import com.anotasmart.ui.viewModels.DespesasViewModel
import java.text.SimpleDateFormat
import java.util.*

import androidx.compose.ui.platform.LocalContext
import com.anotasmart.AnotaSmartApplication
import com.anotasmart.ui.viewModels.CategoriasViewModelFactory

@Composable
fun DespesasScreen(
    viewModel: DespesasViewModel = viewModel()
) {
    val context = LocalContext.current
    val database = (context.applicationContext as AnotaSmartApplication).database
    val categoriasViewModel: CategoriasViewModel = viewModel(
        factory = CategoriasViewModelFactory(database.categoryDao())
    )
    val expenses by viewModel.expenses.collectAsState()
    val categories by categoriasViewModel.categoriasDespesas.collectAsState(initial = emptyList())
    val showAddModal by viewModel.showAddModal.collectAsState()

    val groupedExpenses = viewModel.getGroupedExpenses()
    val expandedStates = remember { mutableStateMapOf<String, Boolean>() }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            groupedExpenses.forEach { (monthYear, monthExpenses) ->
                item {
                    val isExpanded = expandedStates[monthYear] ?: true
                    MonthHeader(
                        monthYear = monthYear,
                        subtotal = viewModel.getMonthSubtotal(monthYear),
                        isExpanded = isExpanded,
                        onToggle = { expandedStates[monthYear] = !isExpanded }
                    )
                }

                if (expandedStates[monthYear] ?: true) {
                    items(monthExpenses) { expense ->
                        ExpenseCard(
                            expense = expense,
                            categoryName = categories.find { it.id == expense.categoryId }?.nome ?: "Sem categoria"
                        )
                    }
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick = { viewModel.openAddModal() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Adicionar Despesa")
        }

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
fun MonthHeader(
    monthYear: String,
    subtotal: Double,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = monthYear,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Subtotal: R$ ${String.format("%.2f", subtotal)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun ExpenseCard(expense: Expense, categoryName: String) {
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR")) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        )
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.description,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SuggestionChip(
                        onClick = { },
                        label = { Text(
                            categoryName,
                            style = MaterialTheme.typography.labelSmall
                            ,
                        ) },
                        modifier = Modifier.height(20.dp)
                    )
                    Text(
                        text = dateFormatter.format(Date(expense.date)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = "R$ ${String.format("%.2f", expense.amount)}",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
